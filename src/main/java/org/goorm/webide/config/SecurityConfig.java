package org.goorm.webide.config;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.auth.JwtFilter;
import org.goorm.webide.auth.JwtUtil;
import org.goorm.webide.domain.User;
import org.goorm.webide.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {
  private final JwtFilter jwtFilter;
  private final JwtUtil jwtUtil;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception{
   return http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers( "/api/login", "/api/sign-up").permitAll()
                .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/api/logout").hasAnyAuthority("USER", "ADMIN")
                .anyRequest().permitAll() // 서블릿은 허용해주는데 jwt 필터는 적용됨
        )
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
//        .oauth2ResourceServer(
//            oauth2 -> oauth2.jwt(jwt -> jwt.decoder(JwtDecoders.fromIssuerLocation("http://localhost:8080/auth/realms/goorm")))
//        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(config -> {
          config.logoutUrl("/api/logout");
          config.logoutSuccessUrl("/api/login");
          config.addLogoutHandler(
              (request, response, auth) ->
              {
                String token = request.getHeader("Authorization").substring(7);

                try {
                  jwtUtil.validateToken(token);
                } catch (Exception e) {
                  jwtUtil.handleJwtException(e, response);
                  return;
                }

                String userEmail = jwtUtil.extractUserEmail(token);
                User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("사용자를 확인할 수 없습니다."));
                boolean isRefreshTokenExpired = user.getIsRefreshTokenExpired();

                if(isRefreshTokenExpired){
                  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                  response.setContentType("application/json");
                  response.setCharacterEncoding("UTF-8");
                  try {
                    response.getWriter().write("{\"error\": \"비정상적인 접근입니다\"}");
                    return;
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                }

                // 로그아웃 처리
                user.setIsRefreshTokenExpired(true);
                userRepository.save(user);
              }
          );
          config.logoutSuccessHandler(
              (request, response, auth) ->
              {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                  response.getWriter().write("{\"message\": \"로그아웃 되었습니다.\"}");
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
          );
        })
        .build();
  }
}

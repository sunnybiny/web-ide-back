package org.goorm.webide.auth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final JwtProvider jwtProvider;

  private static final String JWT_TOKEN_PREFIX = "Bearer ";
  private final UserRepository userRepository;


  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String servletPath = request.getServletPath();
    if (servletPath.equals("/api/login") || servletPath.startsWith("/api/auth/") || servletPath.equals("/api/sign-up")) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorizationHeader = request.getHeader(AUTHORIZATION);

    // 헤더에 토큰이 없음
    if (authorizationHeader == null || !authorizationHeader.startsWith(JWT_TOKEN_PREFIX)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write("{\"error\": \"요청에서 Token 정보를 확인할 수 없습니다.\"}");
      return;
    }

    String token = authorizationHeader.substring(JWT_TOKEN_PREFIX.length());


    // 토큰이 잘못되었음
    try {
      if (!jwtUtil.validateToken(token)) {
        // 이 부분은 실제로 도달하지 않음, 예외가 발생할 것이기 때문
        return;
      }
    } catch (Exception e) {
      jwtUtil.handleJwtException(e, response);
      return;
    }

    String tokenType = jwtUtil.extractTokenType(token);

    if ("access".equals(tokenType)) {
      Authentication auth = jwtProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    } else if ("refresh".equals(tokenType)) {
      boolean isRefreshTokenExpired = userRepository.findByRefreshToken(token)
          .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 존재하지 않습니다."))
          .getIsRefreshTokenExpired();

      if (isRefreshTokenExpired) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Refresh Token이 만료되었습니다.\"}");
        return;
      }

      Map<String, String> tokens = jwtProvider.refresh(token);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(
    "{\"access_token\": \"" +
      JWT_TOKEN_PREFIX +
      tokens.get("access") +
      "\", \"refresh_token\": \"" + 
      JWT_TOKEN_PREFIX + 
      tokens.get("refresh") +
      "\"}"
      );
      Authentication auth = jwtProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    } else {
      // 토큰 타입이 유효하지 않은 경우
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType(APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write("{\"error\": \"유효하지 않은 토큰 타입입니다.\"}");
      return;
    }
  }
}

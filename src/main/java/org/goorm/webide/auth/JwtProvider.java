package org.goorm.webide.auth;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.User;
import org.goorm.webide.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;


  public Authentication getAuthentication(String token){
    String userEmail = jwtUtil.extractUserEmail(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

    if (userDetails == null) {
      log.error("인증된 유저 정보가 없습니다 : {}", userEmail);
      throw new UsernameNotFoundException("인증된 유저 정보가 없습니다");
    }

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }


  @Transactional
  public Map<String, String> refresh(String refreshToken) {
    Map<String, String> tokenResponseMap = new HashMap<>();

    Date expirationTime = jwtUtil.extractExpiration(refreshToken);
    String userEmail = jwtUtil.extractUserEmail(refreshToken);
    User user = userRepository.findByRefreshToken(refreshToken)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    if(!userEmail.equals(user.getEmail())){
      throw new IllegalArgumentException("토큰의 사용자와 일치하지 않습니다.");
    }
    String accessToken = jwtUtil.generateAccessToken(user.getEmail());

    if (expirationTime.before(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))){
      String newRefreshToken = jwtUtil.genearteRefreshToken(user.getEmail());
      user.setRefreshToken(newRefreshToken);
      userRepository.save(user);
      tokenResponseMap.put("refresh_token", newRefreshToken);
    }

    tokenResponseMap.put("access_token", accessToken);
    tokenResponseMap.put("refresh_token", refreshToken);
    return tokenResponseMap;
    }
  }


package org.goorm.webide.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.repository.UserRepository;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil{
  private final String SECRET_KEY = "d3200099cc45ea9c4685310c9057c4c1a301090773524c40b23b9538fee3a4d6056a149c7df720b61f3f1b08a3da38594179b0b429d6df9950180496977a08c3";
  private final Long ACCESS_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 30 ; // 30분
  private final Long REPRESH_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 60 * 24 * 7; // 1주
  private final UserRepository userRepository;

  public String extractUserEmail(String token){
    return extractClaim(token, claims -> claims.get("sub", String.class));
  }

  public Date extractExpiration(String token){
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(SECRET_KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String generateAccessToken(String userEmail){
    return createToken(userEmail, "access", ACCESS_TOKEN_EXPIRATION_PERIOD);
  }

  public String genearteRefreshToken(String userEmail){
    return createToken(userEmail,"refresh", REPRESH_TOKEN_EXPIRATION_PERIOD);
  }

  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get("type", String.class));
  }


  public boolean validateToken(String token) throws JwtException {
    try {
      extractAllClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw new ExpiredJwtException(null, null, "토큰이 만료되었습니다.");
    } catch (UnsupportedJwtException e) {
      throw new UnsupportedJwtException("지원되지 않는 토큰입니다.");
    } catch (MalformedJwtException e) {
      throw new MalformedJwtException("잘못된 토큰입니다.");
    } catch (SignatureException e) {
      throw new SignatureException("토큰 서명이 유효하지 않습니다.");
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("토큰이 비어있습니다.");
    }catch (RuntimeException e) {
      throw new RuntimeException("서버에서 알 수 없는 오류가 발생하였습니다.");
    }
  }

  public void handleJwtException(Exception e, HttpServletResponse response) {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    try {
      if (e instanceof ExpiredJwtException) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"토큰이 만료되었습니다.\"}");
        return;
      }
      if (e instanceof UnsupportedJwtException) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"지원되지 않는 토큰입니다.\"}");
        return;
      }
      if (e instanceof MalformedJwtException) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"잘못된 토큰입니다.\"}");
        return;
      }
      if (e instanceof SignatureException) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"토큰 서명이 유효하지 않습니다.\"}");
        return;
      }
      if (e instanceof IllegalArgumentException) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"토큰이 비어있습니다.\"}");
        return;
      }
      if (e instanceof RuntimeException) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"error\": \"서버에서 알 수 없는 오류가 발생하였습니다.\"}");
        return;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private String createToken(String subject, String tokenType, Long expirationPeriod){
    Claims claims = Jwts.claims()
        .setSubject(subject);
    claims.put("type", tokenType);

    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(new Date(System.currentTimeMillis() + expirationPeriod))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  private boolean isValidBase64Url(String token) {
    try {
      Base64.getUrlDecoder().decode(token);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}

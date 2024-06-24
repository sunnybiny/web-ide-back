package org.goorm.webide.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfig{
  @Bean
  public BCryptPasswordEncoder passwordEncoder() { // 비밀번호 암호화
    return new BCryptPasswordEncoder();
  }
}
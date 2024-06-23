package org.goorm.webide.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
  MEMBER("ROLE_USER", "일반사용자"),
  LEADER("ROLE_ADMIN", "일반관리자");

  private final String key;
  private final String title;

}

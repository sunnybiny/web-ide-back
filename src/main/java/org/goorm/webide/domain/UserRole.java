package org.goorm.webide.domain;

public enum UserRole
{
  USER("USER"), ADMIN("ADMIN");

  private final String roleName;

  UserRole(String roleName)
  {
    this.roleName = roleName;
  }
}

package org.goorm.webide.dto.requestDto;

public enum LanguageType {
  PYTHON("py"),
  JAVA("java"),
  JAVASCRIPT("js");

  private final String value;

  LanguageType(String value) {
    this.value = value;
  }
}

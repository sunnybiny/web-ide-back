package org.goorm.webide.dto.responseDto;


import org.goorm.webide.domain.User;

public record UserDto(Long id, String name,  String email){
  public UserDto(User user){
    this(user.getId(), user.getName(), user.getEmail());
  }
}

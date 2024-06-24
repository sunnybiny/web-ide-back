package org.goorm.webide.dto.responseDto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.goorm.webide.domain.User;

@JsonNaming(SnakeCaseStrategy.class)
public record UserDto(Long id, String username,  String email){
  public UserDto(User user){
    this(user.getId(), user.getName(), user.getEmail());
  }
}

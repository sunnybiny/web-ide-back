package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.goorm.webide.domain.User;

@Data
@JsonNaming(SnakeCaseStrategy.class)
public class ProjectUserDto {
  private Long id;
  private String name;
  private boolean isOnline;

  public ProjectUserDto(User user, boolean isOnline) {
    this.id = user.getId();
    this.name = user.getName();
    this.isOnline = isOnline;
  }
}

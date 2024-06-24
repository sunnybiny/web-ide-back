package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(SnakeCaseStrategy.class)
public class ProjectUserUpdateDto {
  private Long id;
  private boolean isOnline;

  public ProjectUserUpdateDto(Long userId, boolean isOnline) {
    this.id = userId;
    this.isOnline = isOnline;
  }
}

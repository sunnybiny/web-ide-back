package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import org.goorm.webide.domain.Project;

@JsonNaming(SnakeCaseStrategy.class)
public record ProjectDto(Long id, String name, String containedId, LocalDateTime createdAt, LocalDateTime updatedAt, UserDto createdBy){
  public ProjectDto(Project project){
    this(project.getId(),
        project.getName(),
        String.valueOf(project.getContainer().getId()),
        project.getCreatedAt(),
        project.getUpdatedAt(),
        new UserDto(project.getCreatedBy()));
  }
}

package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.Optional;
import org.goorm.webide.domain.Project;
import org.goorm.webide.domain.User;

@JsonNaming(SnakeCaseStrategy.class)
public record ProjectDto(Long id, String name, String containedId, LocalDateTime createdAt, LocalDateTime updatedAt, UserDto createdBy){

  public ProjectDto(Project project, User createdBy){
    this(project.getId(),
        project.getName(),
        Optional.ofNullable(project.getContainer())
                .map(container -> String.valueOf(container.getId()))
                    .orElse(null),
        project.getCreatedAt(),
        project.getUpdatedAt(),
        new UserDto(createdBy));
  }
}

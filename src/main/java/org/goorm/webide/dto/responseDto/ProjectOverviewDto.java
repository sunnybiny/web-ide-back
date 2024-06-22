package org.goorm.webide.dto.responseDto;

import java.time.ZoneOffset;
import java.util.Optional;
import lombok.Data;
import org.goorm.webide.domain.Project;

@Data
public class ProjectOverviewDto {
  private Long id;
  private String name;
  private Long createdAt;
  private Long updatedAt;

  public ProjectOverviewDto(Project project) {
    this.id = project.getId();
    this.name = project.getName();
    this.createdAt = project.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
    this.updatedAt = Optional.ofNullable(project.getUpdatedAt())
        .map(endedAt -> endedAt.toInstant(ZoneOffset.UTC).toEpochMilli())
        .orElse(null);
  }
}

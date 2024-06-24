package org.goorm.webide.dto.requestDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(SnakeCaseStrategy.class)
public class MeetingWriteRequestDto {

  private String title;
  private String description;
}

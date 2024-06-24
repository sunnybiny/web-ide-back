package org.goorm.webide.dto.requestDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Include;

@Data
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class Source {
  private String sourceCode;
  private String languageType;
}

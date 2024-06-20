package org.goorm.webide.model.responseDto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@JsonNaming(SnakeCaseStrategy.class)
public class CodeResult{
  private String standardOutput;
  private String standardError;

  public CodeResult(String standardOutput, String standardError) {
    this.standardOutput = standardOutput;
    this.standardError = standardError;
  }

  public static CodeResult init(){
    return new CodeResult("-", "-");
  }
}

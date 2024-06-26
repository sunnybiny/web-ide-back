package org.goorm.webide.dto.responseDto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.ToString;



@ToString
@JsonNaming(SnakeCaseStrategy.class)
public class CodeResult{
  private StringBuilder standardOutput = new StringBuilder();
  private StringBuilder standardError = new StringBuilder();

  public CodeResult(){}

  public void appendStandardOutput(String output) {
    this.standardOutput.append(output);
  }

  public void appendStandardError(String error) {
    this.standardError.append(error);
  }

  public static CodeResult init(){
    return new CodeResult();
  }

  public String getStandardOutput() {
    return standardOutput.toString();
  }

  public String getStandardError() {
    return standardError.toString();
  }

  public void setStandardOutput(String output) {
    this.standardOutput = new StringBuilder(output);
  }

  public void setStandardError(String error) {
    this.standardError = new StringBuilder(error);
  }
}

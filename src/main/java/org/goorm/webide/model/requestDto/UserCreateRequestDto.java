package org.goorm.webide.model.requestDto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class UserCreateRequestDto {
    public String username ;
    public String password ;
}

package org.goorm.webide.dto.requestDto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class UserCreateRequestDto {

    @NotBlank
    public String username;

    @NotBlank
    public String email;

    @NotBlank
    public String password;
}

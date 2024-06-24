package org.goorm.webide.dto.requestDto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class UserSignupRequestDto {
    @NotBlank
    @Length(min = 3, max = 20)
    private String username;

    @NotBlank
//    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$")
    private String password;

    @NotBlank
    @Email
    private String email;
}

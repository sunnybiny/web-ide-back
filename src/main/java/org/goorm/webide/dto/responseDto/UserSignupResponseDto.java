package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record UserSignupResponseDto(Long id, String username, String email, LocalDateTime createdAt){}

package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.goorm.webide.domain.UserRole;

@JsonNaming(SnakeCaseStrategy.class)
public record UserLoginResponseDto(Long id, String username, String email, String accessToken, String refreshToken, UserRole userRole){}

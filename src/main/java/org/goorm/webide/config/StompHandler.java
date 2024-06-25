package org.goorm.webide.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.auth.JwtProvider;
import org.goorm.webide.domain.User;
import org.goorm.webide.repository.WebSocketSessionRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

  private static final String BEARER_TOKEN_PREFIX = "Bearer ";
  private final JwtProvider jwtTokenProvider;
  private final WebSocketSessionRepository webSocketSessionRepository;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor.getCommand() == StompCommand.CONNECT) {
      String jwtToken = resolveJwtToken(accessor);
      Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
      String sessionId = accessor.getSessionId();
      User user = (User) authentication.getPrincipal();

      Long currentSessionUserId = webSocketSessionRepository.findSessionUser(sessionId);
      if (currentSessionUserId != null && !currentSessionUserId.equals(user.getId())) {
        throw new IllegalArgumentException("현재 세션에 이미 인증됬던 사용자가 있습니다");
      }
      webSocketSessionRepository.addSessionUser(sessionId, user.getId());
    }

    return message;
  }

  private String resolveJwtToken(StompHeaderAccessor accessor) {
    String bearerToken = accessor.getFirstNativeHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
      return bearerToken.substring(BEARER_TOKEN_PREFIX.length());
    }
    return null;
  }
}

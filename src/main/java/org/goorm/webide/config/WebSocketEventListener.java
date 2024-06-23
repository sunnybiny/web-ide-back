package org.goorm.webide.config;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.dto.responseDto.ProjectUserUpdateDto;
import org.goorm.webide.repository.WebSocketSessionRepository;
import org.goorm.webide.service.LiveUserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final WebSocketSessionRepository webSocketSessionRepository;
  private final LiveUserService liveUserService;
  private final SimpMessagingTemplate template;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();
    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//    String httpSessionId = sessionAttributes.get("HTTP_SESSION_ID").toString();
    Principal user = accessor.getUser();
    //Long userId = Long.valueOf(user.getName());
    Long userId = 1L;

    webSocketSessionRepository.addSessionUser(sessionId, userId);

//    webSocketSessionRepository.addSessionToHttpSession(httpSessionId, sessionId);
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();
    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//    String httpSessionId = sessionAttributes.get("HTTP_SESSION_ID").toString();
    Principal user = accessor.getUser();
//    Long userId = Long.valueOf(user.getName());
    Long userId = 1L;

    webSocketSessionRepository.removeSessionUser(sessionId);

    List<Long> leftProjectIds = liveUserService.leaveAllProjects(sessionId, userId);
    for (Long leftProjectId : leftProjectIds) {
      template.convertAndSend("/topic/projects/" + leftProjectId + "/userUpdates", List.of(new ProjectUserUpdateDto(userId, false)));
    }

//    webSocketSessionRepository.removeSessionFromHttpSession(httpSessionId, sessionId);

  }
}
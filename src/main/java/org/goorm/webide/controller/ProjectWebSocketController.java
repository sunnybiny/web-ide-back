package org.goorm.webide.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.responseDto.ProjectUserUpdateDto;
import org.goorm.webide.repository.WebSocketSessionRepository;
import org.goorm.webide.service.LiveUserService;
import org.goorm.webide.service.ProjectService;
import org.goorm.webide.service.UserProjectService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectWebSocketController {

  private final LiveUserService liveUserService;
  private final UserProjectService userProjectService;
  private final SimpMessagingTemplate template;
  private final WebSocketSessionRepository webSocketSessionRepository;

  @MessageMapping("/projects/{projectId}/join")
  public void join(@DestinationVariable Long projectId,
      SimpMessageHeaderAccessor accessor) {

    String sessionId = accessor.getSessionId();
    Long userId = webSocketSessionRepository.findSessionUser(sessionId);

    liveUserService.joinProject(projectId, sessionId, userId);
    List<ProjectUserUpdateDto> userUpdates = List.of(new ProjectUserUpdateDto(userId, true));
    template.convertAndSend("/topic/projects/" + projectId + "/userUpdates", userUpdates);
  }

  @MessageMapping("/projects/{projectId}/leave")
  public void leave(@DestinationVariable Long projectId,
      SimpMessageHeaderAccessor accessor) {

    String sessionId = accessor.getSessionId();
    Long userId = webSocketSessionRepository.findSessionUser(sessionId);

    Boolean isUserLeftProject = liveUserService.leaveProject(projectId, sessionId, userId);
    if (isUserLeftProject) {
      List<ProjectUserUpdateDto> userUpdates = List.of(new ProjectUserUpdateDto(userId, false));
      template.convertAndSend("/topic/projects/" + projectId + "/userUpdates", userUpdates);
    }
  }

  @SubscribeMapping("/topic/projects/{projectId}/userUpdates")
  public void subscribeUserUpdates(@DestinationVariable Long projectId, SimpMessageHeaderAccessor accessor) {
    Long userId = webSocketSessionRepository.findSessionUser(accessor.getSessionId());
    userProjectService.findUserProject(projectId, userId);
  }

  @SubscribeMapping("/topic/projects/{projectId}/newMeeting")
  public void subscribeNewMeeting(@DestinationVariable Long projectId, SimpMessageHeaderAccessor accessor) {
    Long userId = webSocketSessionRepository.findSessionUser(accessor.getSessionId());
    userProjectService.findUserProject(projectId, userId);
  }
}

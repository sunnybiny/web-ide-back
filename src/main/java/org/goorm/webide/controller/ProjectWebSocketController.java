package org.goorm.webide.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.dto.responseDto.ProjectUserUpdateDto;
import org.goorm.webide.service.LiveUserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectWebSocketController {

  private final LiveUserService liveUserService;
  private final SimpMessagingTemplate template;


  @MessageMapping("/projects/{projectId}/join")
  public void join(@DestinationVariable Long projectId, SimpMessageHeaderAccessor headerAccessor, Authentication authentication) {
    String sessionId = headerAccessor.getSessionId();
    //Long userId = Long.valueOf(authentication.getName());
    Long userId = 1L;

    liveUserService.joinProject(projectId, sessionId, userId);
    template.convertAndSend("/topic/projects/" + projectId + "/userUpdates", List.of(new ProjectUserUpdateDto(userId, true)));
  }

  @MessageMapping("/projects/{projectId}/leave")
  public void leave(@DestinationVariable Long projectId, SimpMessageHeaderAccessor headerAccessor, Authentication authentication) {
    String sessionId = headerAccessor.getSessionId();
    // Long userId = Long.valueOf(authentication.getName());
    Long userId = 1L;

    Boolean isUserLeftProject = liveUserService.leaveProject(projectId, sessionId, userId);
    if (isUserLeftProject) {
      template.convertAndSend("/topic/projects/" + projectId + "/userUpdates", List.of(new ProjectUserUpdateDto(userId, false)));
    }
  }
}

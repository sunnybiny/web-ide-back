package org.goorm.webide.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.repository.WebSocketSessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveUserService {

  private final UserProjectService userProjectService;
  private final WebSocketSessionRepository webSocketSessionRepository;


  public void joinProject(Long projectId, String sessionId, Long userId) {
    userProjectService.findUserProject(projectId, userId);
    webSocketSessionRepository.addSessionToProject(projectId, sessionId, userId);
  }

  public Boolean leaveProject(Long projectId, String sessionId, Long userId) {
    return webSocketSessionRepository.removeSessionFromProject(projectId, sessionId, userId);
  }

  public List<Long> leaveAllProjects(String sessionId, Long userId) {
    return webSocketSessionRepository.removeSessionFromALlProjects(sessionId, userId);
  }

  public Set<Long> findOnlineUserIdsByProjectId(Long projectId) {
    return webSocketSessionRepository.findUserIdsByProjectId(projectId);
  }

}

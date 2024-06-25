package org.goorm.webide.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class WebSocketSessionRepository {

  private final Map<Long, Set<String>> sessionIdsByProjectId = new ConcurrentHashMap<>();
  private final Map<String, Set<String>> sessionIdsByProjectUserId = new ConcurrentHashMap<>();
  private final Map<String, Set<Long>> projectIdsBySessionId = new ConcurrentHashMap<>();

  private final Map<String, Long> userIdBySessionId = new ConcurrentHashMap<>();


  private String createProjectUserIdKey(Long projectId, Long userId) {
    return projectId + "_" + userId;
  }

  public synchronized void addSessionToProject(Long projectId, String sessionId, Long userId) {
    sessionIdsByProjectId.computeIfAbsent(projectId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    projectIdsBySessionId.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(projectId);

    String projectUserIdKey = createProjectUserIdKey(projectId, userId);
    sessionIdsByProjectUserId.computeIfAbsent(projectUserIdKey, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
  }

  public synchronized Boolean removeSessionFromProject(Long projectId, String sessionId, Long userId) {
    Set<String> projectSessionIds = sessionIdsByProjectId.get(projectId);
    if (projectSessionIds != null) {
      projectSessionIds.remove(sessionId);
      if (projectSessionIds.isEmpty()) {
        sessionIdsByProjectId.remove(projectId);
      }
    }

    Set<Long> sessionProjectIds = projectIdsBySessionId.get(sessionId);
    if (sessionProjectIds != null) {
      sessionProjectIds.remove(projectId);
      if (sessionProjectIds.isEmpty()) {
        projectIdsBySessionId.remove(sessionId);
      }
    }

    String projectUserIdKey = createProjectUserIdKey(projectId, userId);
    Set<String> projectUserSessionIds = sessionIdsByProjectUserId.get(projectUserIdKey);
    if (projectUserSessionIds != null) {
      projectUserSessionIds.remove(sessionId);
      if (projectUserSessionIds.isEmpty()) {
        sessionIdsByProjectUserId.remove(projectUserIdKey);
        return true;
      }
    }

    return false;
  }

  public synchronized List<Long> removeSessionFromALlProjects(String sessionId, Long userId) {
    List<Long> leftProjectIds = new ArrayList<>();

    Set<Long> sessionProjectIds = projectIdsBySessionId.getOrDefault(sessionId, new HashSet<>());

    for (Long projectId : sessionProjectIds) {
      Set<String> projectSessionIds = sessionIdsByProjectId.get(projectId);
      if (projectSessionIds != null) {
        projectSessionIds.remove(sessionId);
        if (projectSessionIds.isEmpty()) {
          sessionIdsByProjectId.remove(projectId);
        }
      }

      String projectUserIdKey = createProjectUserIdKey(projectId, userId);
      Set<String> projectUserSessionIds = sessionIdsByProjectUserId.get(projectUserIdKey);
      if (projectUserSessionIds != null) {
        projectUserSessionIds.remove(sessionId);
        if (projectUserSessionIds.isEmpty()) {
          sessionIdsByProjectUserId.remove(projectUserIdKey);
          leftProjectIds.add(projectId);
        }
      }
    }

    projectIdsBySessionId.remove(sessionId);

    return leftProjectIds;
  }

  public Set<Long> findUserIdsByProjectId(Long projectId) {
    Set<Long> userIds = new HashSet<>();
    Set<String> sessionIds = sessionIdsByProjectId.get(projectId);
    if (sessionIds == null) {
      return userIds;
    }

    for (String sessionId : sessionIds) {
      Long userId = userIdBySessionId.get(sessionId);
      if (userId != null) {
        userIds.add(userId);
      }
    }

    return userIds;
  }

  public void addSessionUser(String sessionId, Long userId) {
    userIdBySessionId.put(sessionId, userId);
  }

  public Long findSessionUser(String sessionId) {
    return userIdBySessionId.get(sessionId);
  }

  public Long removeSessionUser(String sessionId) {
    return userIdBySessionId.remove(sessionId);
  }

}

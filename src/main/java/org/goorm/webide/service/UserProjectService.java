package org.goorm.webide.service;

import lombok.RequiredArgsConstructor;
import org.goorm.webide.domain.UserProject;
import org.goorm.webide.repository.UserProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserProjectService {

  private final UserProjectRepository userProjectRepository;

  public UserProject findUserProject(Long projectId, Long userId) {
    return userProjectRepository.findByUserIdAndProjectId(userId, projectId)
        .orElseThrow(() -> new AccessDeniedException("프로젝트의 멤버가 아닙니다."));
  }
}

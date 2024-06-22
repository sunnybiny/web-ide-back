package org.goorm.webide.repository;

import org.goorm.webide.domain.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long>{

  Optional<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);

  boolean existsByUserIdAndProjectName(Long userId, String projectName);

  @Transactional
  void deleteByProjectId(Long projectId);
}

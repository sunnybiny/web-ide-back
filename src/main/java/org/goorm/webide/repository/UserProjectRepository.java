package org.goorm.webide.repository;

import java.util.List;
import org.goorm.webide.domain.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long>{

  Optional<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);

  boolean existsByUserIdAndProjectName(Long userId, String projectName);

  List<UserProject> findByUserId(Long userId);

  @Transactional
  void deleteByProjectId(Long projectId);
}

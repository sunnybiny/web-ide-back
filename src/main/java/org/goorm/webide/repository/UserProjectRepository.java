package org.goorm.webide.repository;

import java.util.List;
import java.util.Optional;
import org.goorm.webide.domain.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long>{

  Optional<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);
  Optional<UserProject> findByUserId(Long userId);
  List<UserProject> findAllByUserId(Long userId);

  boolean existsByUserIdAndProjectName(Long userId, String projectName);

  @Transactional
  void deleteByProjectId(Long projectId);

  boolean existsByUserIdAndProjectId(Long id, Long projectId);
}

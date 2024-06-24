package org.goorm.webide.repository;

import java.util.List;
import org.goorm.webide.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByName(String name);

    @Transactional
    Project findByContainerId(String containerId);

    @Query("SELECT p FROM Project p JOIN FETCH p.userProjects up WHERE up.user.id = :userId")
    List<Project> findAllByUserId(Long userId);
}

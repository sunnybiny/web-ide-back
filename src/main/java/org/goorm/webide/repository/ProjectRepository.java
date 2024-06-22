package org.goorm.webide.repository;

import org.goorm.webide.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByName(String name);

    @Transactional
    Project findByContainerId(String containerId);
}

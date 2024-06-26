package org.goorm.webide.repository;

import java.util.List;
import java.util.Optional;
import org.goorm.webide.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

  Optional<Meeting> findByProjectIdAndEndedAtIsNull(Long projectId);

  List<Meeting> findByProjectId(Long projectId);
}
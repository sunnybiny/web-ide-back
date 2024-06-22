package org.goorm.webide.repository;

import java.util.List;
import org.goorm.webide.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findByMeetingId(Long meetingId);
}
package org.goorm.webide.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.ChatMessage;
import org.goorm.webide.domain.Meeting;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.responseDto.ChatMessageDto;
import org.goorm.webide.repository.ChatMessageRepository;
import org.goorm.webide.repository.MeetingRepository;
import org.goorm.webide.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

  private final UserRepository userRepository;
  private final MeetingRepository meetingRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserProjectService userProjectService;

  @Transactional
  public ChatMessageDto saveMessage(Long meetingId, Long senderId, String text) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의입니다."));

    User user = userRepository.findById(senderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    userProjectService.findUserProject(meeting.getProject().getId(), user.getId());

    if (meeting.isEnded()) {
      throw new IllegalStateException("이미 종료된 회의입니다.");
    }

    ChatMessage chatMessage = new ChatMessage(user, meeting, text);

    chatMessageRepository.save(chatMessage);

    return new ChatMessageDto(chatMessage);
  }

  public List<ChatMessageDto> findMessages(Long meetingId, Long userId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의입니다."));

    userProjectService.findUserProject(meeting.getProject().getId(), userId);

    return chatMessageRepository.findByMeetingId(meetingId)
        .stream()
        .map(ChatMessageDto::new)
        .toList();
  }

}
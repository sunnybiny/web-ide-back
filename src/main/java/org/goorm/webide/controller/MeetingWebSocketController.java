package org.goorm.webide.controller;

import java.security.Principal;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.responseDto.ChatMessageDto;
import org.goorm.webide.repository.WebSocketSessionRepository;
import org.goorm.webide.service.ChatService;
import org.goorm.webide.service.MeetingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MeetingWebSocketController {

  private final ChatService chatService;
  private final MeetingService meetingService;
  private final WebSocketSessionRepository webSocketSessionRepository;

  @MessageMapping("/meetings/{meetingId}/chat")
  @SendTo("/topic/meetings/{meetingId}/chat")
  public ChatMessageDto sendChat(@DestinationVariable Long meetingId,
      @Payload SendChatRequestDto requestDto,
      SimpMessageHeaderAccessor accessor) {

    String sessionId = accessor.getSessionId();

    System.out.println("requestDto = " + requestDto);

    Long senderId = webSocketSessionRepository.findSessionUser(accessor.getSessionId());
    String text = requestDto.getText();

    return chatService.saveMessage(meetingId, senderId, text);
  }

  @SubscribeMapping("/topic/meetings/{meetingId}/chat")
  public void subscribeChat(@DestinationVariable Long meetingId, SimpMessageHeaderAccessor accessor) {
    Long userId = webSocketSessionRepository.findSessionUser(accessor.getSessionId());
    meetingService.findMeeting(meetingId, userId);
  }

  @SubscribeMapping("/topic/meetings/{meetingId}/update")
  public void subscribeUpdate(@DestinationVariable Long meetingId, SimpMessageHeaderAccessor accessor) {
    Long userId = webSocketSessionRepository.findSessionUser(accessor.getSessionId());
    meetingService.findMeeting(meetingId, userId);
  }

  @Data
  static class SendChatRequestDto {
    private String text;
  }


}

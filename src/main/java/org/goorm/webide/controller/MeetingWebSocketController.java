package org.goorm.webide.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.dto.ChatMessageDto;
import org.goorm.webide.service.ChatService;
import org.goorm.webide.service.MeetingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MeetingWebSocketController {

  private final ChatService chatService;
  private final MeetingService meetingService;

  @MessageMapping("/meetings/{meetingId}/chat")
  @SendTo("/topic/meetings/{meetingId}/chat")
  public ChatMessageDto sendChat(@DestinationVariable Long meetingId,
      @Payload SendChatRequestDto requestDto) {

    Long senderId = requestDto.getSenderId();
    String text = requestDto.getText();

    return chatService.saveMessage(meetingId, senderId, text);
  }

  @Data
  static class SendChatRequestDto {

    private Long senderId;
    private String text;
  }


}

package org.goorm.webide.dto;

import java.time.ZoneOffset;
import lombok.Data;
import org.goorm.webide.domain.ChatMessage;

@Data
public class ChatMessageDto {

  private Long messageId;
  private Long senderId;
  private String senderName;
  private String text;
  private Long createdAt;

  public ChatMessageDto(ChatMessage message) {
    this.messageId = message.getId();
    this.senderId = message.getSender().getId();
    this.senderName = message.getSender().getName();
    this.text = message.getText();
    this.createdAt = message.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
  }
}
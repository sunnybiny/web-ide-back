package org.goorm.webide.dto.responseDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.ZoneOffset;
import lombok.Data;
import org.goorm.webide.domain.ChatMessage;

@Data
@JsonNaming(SnakeCaseStrategy.class)
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
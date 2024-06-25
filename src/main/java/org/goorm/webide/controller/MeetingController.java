package org.goorm.webide.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.api.API;
import org.goorm.webide.domain.Meeting;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.MeetingWriteRequestDto;
import org.goorm.webide.dto.responseDto.ChatMessageDto;
import org.goorm.webide.dto.responseDto.MeetingDto;
import org.goorm.webide.service.ChatService;
import org.goorm.webide.service.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

  private final SimpMessagingTemplate template;
  private final MeetingService meetingService;
  private final ChatService chatService;


  @GetMapping("/{meetingId}")
  public API<MeetingDto> getMeeting(@PathVariable Long meetingId, @AuthenticationPrincipal User user) {
    Meeting meeting = meetingService.findMeeting(meetingId, user.getId());
    MeetingDto meetingDto = new MeetingDto(meeting);
    return API.<MeetingDto>builder()
        .data(meetingDto)
        .resultCode(HttpStatus.OK.toString())
        .resultMessage(HttpStatus.OK.getReasonPhrase())
        .build();
  }

  @PatchMapping("/{meetingId}")
  public API<MeetingDto> updateMeeting(@PathVariable Long meetingId, @AuthenticationPrincipal User user,
      @RequestBody MeetingWriteRequestDto requestDto) {

    MeetingDto meeting = meetingService.updateMeeting(meetingId, requestDto, user.getId());
    template.convertAndSend("/topic/meetings/" + meetingId + "/update", meeting);
    return API.<MeetingDto>builder()
        .data(meeting)
        .resultCode(HttpStatus.OK.toString())
        .resultMessage(HttpStatus.OK.getReasonPhrase())
        .build();
  }

  @PostMapping("/{meetingId}/end")
  public API<MeetingDto> endMeeting(@PathVariable Long meetingId, @AuthenticationPrincipal User user) {
    MeetingDto meeting = meetingService.endMeeting(meetingId, user.getId());
    template.convertAndSend("/topic/meetings/" + meetingId + "/update", meeting);
    return API.<MeetingDto>builder()
        .data(meeting)
        .resultCode(HttpStatus.OK.toString())
        .resultMessage(HttpStatus.OK.getReasonPhrase())
        .build();
  }

  @GetMapping("/{meetingId}/chatMessages")
  public API<List<ChatMessageDto>> getChatMessages(@PathVariable Long meetingId,
      @AuthenticationPrincipal User user) {
    List<ChatMessageDto> messages = chatService.findMessages(meetingId, user.getId());
    return API.<List<ChatMessageDto>>builder()
        .data(messages)
        .resultCode(HttpStatus.OK.toString())
        .resultMessage(HttpStatus.OK.getReasonPhrase())
        .build();
  }

}

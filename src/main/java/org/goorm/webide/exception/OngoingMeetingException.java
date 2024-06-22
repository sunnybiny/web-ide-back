package org.goorm.webide.exception;

import lombok.Getter;
import org.goorm.webide.dto.MeetingDto;

@Getter
public class OngoingMeetingException extends RuntimeException {

  private MeetingDto ongoingMeeting;

  public OngoingMeetingException(MeetingDto ongoingMeeting) {
    this.ongoingMeeting = ongoingMeeting;
  }
}
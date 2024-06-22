package org.goorm.webide.exception;

import org.goorm.webide.dto.MeetingDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(OngoingMeetingException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public MeetingDto handleOngoingMeetingException(OngoingMeetingException ex) {
    return ex.getOngoingMeeting();
  }

}
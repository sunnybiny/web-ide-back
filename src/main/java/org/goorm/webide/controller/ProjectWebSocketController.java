package org.goorm.webide.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProjectWebSocketController {

  @MessageMapping("/projects/{projectId}/join")
  public void join(@DestinationVariable Long projectId) {

  }
  @MessageMapping("/projects/{projectId}/leave")
  public void leave(@DestinationVariable Long projectId) {

  }
}

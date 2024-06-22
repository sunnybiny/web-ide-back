package org.goorm.webide.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.api.API;
import org.goorm.webide.dto.MeetingDto;
import org.goorm.webide.dto.MeetingWriteRequestDto;
import org.goorm.webide.dto.responseDto.CodeResult;
import org.goorm.webide.dto.requestDto.Source;
import org.goorm.webide.dto.requestDto.ProjectCreateRequestDto;
import org.goorm.webide.dto.requestDto.ProjectUpdateRequestDto;
import org.goorm.webide.dto.responseDto.ProjectDto;
import org.goorm.webide.service.ContainerService;
import org.goorm.webide.service.MeetingService;
import org.goorm.webide.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private final SimpMessagingTemplate template;
    private final MeetingService meetingService;
    private final ContainerService containerService;

    @GetMapping("/{projectId}/meetings")
    public List<MeetingDto> getMeetings(@PathVariable Long projectId,
        @RequestParam("userId") Long userId) {
        return meetingService.findMeetings(projectId, userId);
    }

    @GetMapping("/{projectId}/meetings/ongoing")
    public MeetingDto getOngoingMeeting(@PathVariable Long projectId,
        @RequestParam("userId") Long userId) {
        return meetingService.findOngoingMeeting(projectId, userId);
    }

    @PostMapping("/{projectId}/meetings")
    public MeetingDto createMeeting(@PathVariable Long projectId,
        @RequestParam("userId") Long userId,
        @RequestBody MeetingWriteRequestDto requestDto) {

        MeetingDto meeting = meetingService.createMeeting(projectId, requestDto, userId);
        template.convertAndSend("/topic/projects/" + projectId + "/newMeeting", meeting);
        return meeting;
    }

    @GetMapping("/{projectId}")
    public API<ProjectDto> find(@PathVariable Long projectId) {
        ProjectDto projectDto = projectService.find(projectId);
        API<ProjectDto> api = API.<ProjectDto>builder()
                .data(projectDto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PostMapping
    public API<ProjectDto> create(@RequestBody ProjectCreateRequestDto request){
        //TODO : User 가 존재하지 않는 경우 예외처리
        // 세션이 구현이 안되서 일단 아이디로 테스트
        Long userId = 1L;

        ProjectDto projectDto = projectService.create(request.getProjectName(), userId);
        API<ProjectDto> api = API.<ProjectDto>builder()
                .data(projectDto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<API<RuntimeException>> handleIllegalArgumentException(IllegalArgumentException e) {
        API<RuntimeException> api = API.<RuntimeException>builder()
                .resultCode(HttpStatus.BAD_REQUEST.toString())
                .resultMessage(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<API<RuntimeException>> handleIllegalStateException(IllegalStateException e) {
        API<RuntimeException> api = API.<RuntimeException>builder()
                .resultCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .resultMessage(e.getMessage()) // TODO : 에러 메시지 수정
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(api);
    }


    @PatchMapping("/{projectId}")
    public API<ProjectDto> update(@RequestBody ProjectUpdateRequestDto request, @PathVariable Long projectId) {
        ProjectDto projectDto = projectService.update(request.getProjectName(), projectId);
        API<ProjectDto> api = API.<ProjectDto>builder()
                .data(projectDto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @DeleteMapping("/{projectId}")
    public API<?> delete(@PathVariable Long projectId) {
        projectService.delete(projectId);

        API<?> api = API.builder()
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PostMapping("/{projectId}/container/run")
    public API<CodeResult> execContainer(@PathVariable Long projectId, @RequestBody Source source) {
        CodeResult codeResult = containerService.runPythonCode(projectId, source);
        API<CodeResult> api = API.<CodeResult>builder()
                .data(codeResult)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }
}

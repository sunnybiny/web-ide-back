package org.goorm.webide.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.domain.Meeting;
import org.goorm.webide.domain.UserProject;
import org.goorm.webide.domain.UserRole;
import org.goorm.webide.dto.MeetingDto;
import org.goorm.webide.dto.MeetingWriteRequestDto;
import org.goorm.webide.exception.OngoingMeetingException;
import org.goorm.webide.repository.MeetingRepository;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingService {

  private final MeetingRepository meetingRepository;
  private final ProjectRepository projectRepository;
  private final UserProjectRepository userProjectRepository;

  @Transactional
  public MeetingDto createMeeting(Long projectId, MeetingWriteRequestDto requestDto, Long userId) {
    UserProject userProject = validateAndGetUserProject(projectId, userId);

    validateProjectLeader(userProject);

    Optional<Meeting> ongoingMeeting = meetingRepository.findByProjectIdAndEndedAtIsNull(projectId);
    if (ongoingMeeting.isPresent()) {
      throw new OngoingMeetingException(new MeetingDto(ongoingMeeting.get()));
    }

    Meeting meeting = new Meeting(requestDto.getTitle(), requestDto.getDescription());
    meeting.setProject(projectRepository.getReferenceById(projectId));

    meetingRepository.save(meeting);

    return new MeetingDto(meeting);
  }

  public Meeting findMeeting(Long meetingId, Long userId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의입니다."));

    validateAndGetUserProject(meeting.getProject().getId(), userId);

    return meeting;
  }

  public List<MeetingDto> findMeetings(Long projectId, Long userId) {
    validateAndGetUserProject(projectId, userId);

    return meetingRepository.findByProjectId(projectId)
        .stream()
        .map(MeetingDto::new)
        .toList();
  }

  @Transactional
  public MeetingDto updateMeeting(Long meetingId, MeetingWriteRequestDto requestDto, Long userId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의입니다."));

    UserProject userProject = validateAndGetUserProject(meeting.getProject().getId(), userId);

    validateProjectLeader(userProject);

    if (meeting.isEnded()) {
      throw new IllegalStateException("이미 종료된 회의입니다.");
    }

    if (StringUtils.hasText(requestDto.getTitle())) {
      meeting.setTitle(requestDto.getTitle());
    }
    if (StringUtils.hasText(requestDto.getDescription())) {
      meeting.setDescription(requestDto.getDescription());
    }

    meetingRepository.save(meeting);

    return new MeetingDto(meeting);
  }

  @Transactional
  public MeetingDto endMeeting(Long meetingId, Long userId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의입니다."));

    UserProject userProject = validateAndGetUserProject(meeting.getProject().getId(), userId);

    validateProjectLeader(userProject);

    if (meeting.isEnded()) {
      throw new IllegalStateException("이미 종료된 회의입니다.");
    }

    meeting.setEndedAt(LocalDateTime.now());

    meetingRepository.save(meeting);

    return new MeetingDto(meeting);
  }

  public MeetingDto findOngoingMeeting(Long projectId, Long userId) {
    // TODO: 프로젝트를 조회했을 때 미팅이 진행중인 지 반환 추가 (미팅이 진행중이지 않으면 null 반환)

    validateAndGetUserProject(projectId, userId);

    Optional<Meeting> ongoingMeeting = meetingRepository.findByProjectIdAndEndedAtIsNull(
        projectId);

    return ongoingMeeting.map(MeetingDto::new).orElse(null);
  }

  private UserProject validateAndGetUserProject(Long projectId, Long userId) {
    return userProjectRepository.findByUserIdAndProjectId(userId, projectId)
        .orElseThrow(() -> new AccessDeniedException("프로젝트의 멤버가 아닙니다."));
  }

  private void validateProjectLeader(UserProject userProject) {
    if (userProject.getRole() != UserRole.LEADER) {
      throw new AccessDeniedException("프로젝트의 관리자가 아닙니다.");
    }
  }

}
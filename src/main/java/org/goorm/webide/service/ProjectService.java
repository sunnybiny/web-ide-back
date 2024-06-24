package org.goorm.webide.service;

import jakarta.ws.rs.ForbiddenException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.Container;
import org.goorm.webide.domain.Project;
import org.goorm.webide.domain.ProjectRole;
import org.goorm.webide.domain.User;
import org.goorm.webide.domain.UserProject;
import org.goorm.webide.dto.requestDto.ProjectJoinDto;
import org.goorm.webide.dto.responseDto.ProjectDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
import org.goorm.webide.dto.responseDto.ProjectUserDto;
import org.goorm.webide.repository.ContainerRepository;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.goorm.webide.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final ContainerService containerService;
    private final ContainerRepository containerRepository;
    private final LiveUserService liveUserService;
    private final UserProjectService userProjectService;

    public ProjectDto find(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        return new ProjectDto(project);
    }

    public List<ProjectOverviewDto> findAll(User user) {
        List<Project> userProjects = projectRepository.findAllByUserId(user.getId());
        return userProjects
            .stream()
            .map(ProjectOverviewDto::new)
            .toList();
    }
    
    @Transactional
    public ProjectDto create(String projectName ,String description, User creator) {

        String imageName = "python:latest";
        Long userId = creator.getId();

        if (userProjectRepository.existsByUserIdAndProjectName(userId, projectName)) {
            throw new IllegalArgumentException("해당 유저가 만든 프로젝트 중에 같은 이름의 프로젝트가 이미 존재합니다.");
        }

        // 도커 컨테이너 이름은 한국어나 특수문자를 사용할 수 없어서 사용자나 프로젝트 이름을 사용할 수 없음
        Container container = containerService.createAndRunContainer(imageName);
        Project project = Project.createProject(projectName, description, container, creator);
        projectRepository.save(project);
        userProjectRepository.save(new UserProject(creator, project, ProjectRole.LEADER));

        return convertToDto(project);
    }

    private ProjectDto convertToDto(Project project) {
        return new ProjectDto(project);
    }

    @Transactional
    public void join(Long projectId, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(
            () -> new IllegalArgumentException("해당 프로젝트가 존재하지 않습니다.")
        );

        if (userProjectRepository.existsByUserIdAndProjectId(user.getId(), projectId)) {
            return;
        }

        userProjectRepository.save(new UserProject(user, project, ProjectRole.MEMBER));
    }

    @Transactional
    public ProjectDto update(String projectName, String description, Long projectId) {

        Project project = projectRepository.findById(projectId).orElseThrow();
        project.setName(projectName);
        project.setDescription(description);
        projectRepository.save(project);

        return convertToDto(project);
    }

    @Transactional
    public void delete(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String containerId = project.getContainer().getId();

        userProjectRepository.deleteByProjectId(projectId);
        projectRepository.deleteById(projectId);
        containerService.cleanContainer(containerId);
        containerRepository.deleteById(containerId);
    }

    public List<ProjectUserDto> findAllUsersByProjectId(Long projectId, Long userId) {
        userProjectService.findUserProject(projectId, userId);

        List<User> users = userRepository.findAllByProjectId(projectId);

        Set<Long> onlineUserIds = liveUserService.findOnlineUserIdsByProjectId(projectId);

        return users.stream()
            .map(user -> {
                boolean isOnline = onlineUserIds.contains(user.getId());
                return new ProjectUserDto(user, isOnline);
            })
            .toList();
    }
}

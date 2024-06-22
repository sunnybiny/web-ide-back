package org.goorm.webide.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.Container;
import org.goorm.webide.domain.Project;
import org.goorm.webide.domain.User;
import org.goorm.webide.domain.UserProject;
import org.goorm.webide.dto.responseDto.ProjectDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
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

    public ProjectDto find(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        return new ProjectDto(project);
    }

    public List<ProjectOverviewDto> findAll(Long userId) {
        List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
        return userProjects
            .stream()
            .map(UserProject::getProject)
            .map(ProjectOverviewDto::new)
            .toList();
    }
    
    @Transactional
    public ProjectDto create(String projectName , Long userId) {

        //TODO : 컨테이너 생성로직
        // 프로젝트와 컨테이너 연결

        String imageName = "python:latest";

        if (userProjectRepository.existsByUserIdAndProjectName(userId, projectName)) {
            throw new IllegalArgumentException("해당 유저가 만든 프로젝트 중에 같은 이름의 프로젝트가 이미 존재합니다.");
        }

        User creator = userRepository.findById(userId).orElseThrow();
        // 도커 컨테이너 이름은 한국어나 특수문자를 사용할 수 없어서 사용자나 프로젝트 이름을 사용할 수 없음
        Container container = containerService.createAndRunContainer(imageName);
        Project project = Project.createProject(projectName, container, creator);

        projectRepository.save(project);
        userProjectRepository.save(new UserProject(creator, project));

        return convertToDto(project);
    }

    private ProjectDto convertToDto(Project project) {
        return new ProjectDto(project);
    }

    @Transactional
    public ProjectDto update(String projectName, Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.setName(projectName);
        projectRepository.save(project);

        return convertToDto(project);
    }

    @Transactional
    public void delete(Long projectId) {
        userProjectRepository.deleteByProjectId(projectId);
        projectRepository.deleteById(projectId);
        String containerId = projectRepository.findById(projectId).orElseThrow().getContainer().getId();
        containerService.cleanContainer(containerId);
        containerRepository.deleteById(containerId);
    }
}

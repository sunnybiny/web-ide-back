package org.goorm.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback.Adapter;
import com.github.dockerjava.api.model.Frame;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.Container;
import org.goorm.webide.model.requestDto.Source;
import org.goorm.webide.model.responseDto.CodeResult;
import org.goorm.webide.repository.ContainerRepository;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerService {
  private final DockerClient dockerClient;
  private final ContainerRepository containerRepository;
  private final ProjectRepository projectRepository;
  private final UserProjectRepository userProjectRepository;

  public Container createAndRunContainer(String imageName){
    String containerName = "" + UUID.randomUUID();
    String containerId = dockerClient.createContainerCmd(imageName)
        .withCmd("/bin/bash")
        .withWorkingDir("/app")
        .withName(containerName)
        .withTty(true)
        .withAttachStdin(true)
        .withAttachStdout(true)
        .withAttachStderr(true)
        .exec()
        .getId();

    dockerClient.startContainerCmd(containerId)
        .exec();

    return containerRepository.save(Container.createContainer(containerId, containerName, imageName));
  }

  public CodeResult   runPythonCode(Long projectId, Source source) {
    StringBuilder sb = new StringBuilder();
    String[] saveSourceCommand = {"bash" , "-c", "echo '" + source.getSourceCode() + "' > main.py"};
    String[] runCommand = {"bash", "-c", "python3 main.py"};// 컴파일 명령

    String containerId = projectRepository.findById(projectId)
        .orElseThrow()
        .getContainer()
        .getId();

    CodeResult saveCodeResult = execCommand(containerId, saveSourceCommand, sb);
    CodeResult runCodeResult = execCommand(containerId, runCommand, sb);

    return runCodeResult;
  }

  private CodeResult execCommand(String containerId, String[] command, StringBuilder sb) {
    String execId = dockerClient.execCreateCmd(containerId)
        .withAttachStdin(true)
        .withAttachStdout(true)
        .withAttachStdout(true)
        .withTty(true)
        .withCmd(command)
        .exec()
        .getId();

    CodeResult codeResult = CodeResult.init();

    try {
      dockerClient.execStartCmd(execId).exec(
          new Adapter<>() {
            @Override
            public void onNext(Frame f) {
              switch (f.getStreamType()) {
                case STDOUT -> {
                  String output = new String(f.getPayload(), StandardCharsets.UTF_8);
                  codeResult.setStandardOutput(output);
                }
                case STDERR -> {
                  String error = new String(f.getPayload(), StandardCharsets.UTF_8);
                  codeResult.setStandardError(error);
                }
                default -> log.error("Unknown stream type: {}", f.getStreamType());
              }
            }
          }
      ).awaitCompletion(30, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error("Interrupted while running code", e);
    }
    log.info("CodeResult: {}", codeResult);
    return codeResult;
  }

  @Profile("local")
  @Transactional
  @PreDestroy
  public void cleanup() {
    log.info("Cleaning up containers...");
    containerRepository.findAll().forEach(container -> {
      try {
        dockerClient.removeContainerCmd(container.getId()).withForce(true).exec();
        userProjectRepository.deleteByProjectId(projectRepository.findByContainerId(container.getId()).getId());
        log.info("Removed container: {}", container.getId());
      } catch (Exception e) {
        log.error("Failed to remove container: {}", container.getId(), e);
      }
    });
  }
}



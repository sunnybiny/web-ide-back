package org.goorm.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback.Adapter;
import com.github.dockerjava.api.model.Frame;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.Container;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.Source;
import org.goorm.webide.dto.responseDto.CodeResult;
import org.goorm.webide.repository.ContainerRepository;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.springframework.stereotype.Service;

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

  public CodeResult runCode(Long projectId, User user, Source source) {
    if (userProjectRepository.findAllByUserId(user.getId()).stream().findAny().isEmpty()) {
      throw new IllegalArgumentException("해당 유저가 만든 프로젝트가 존재하지 않습니다.");
    }

    switch (source.getLanguageType()) {
      case "py" -> {
        return runPythonCode(projectId, source);
      }
      case "java" -> {
        return runJavaCode(projectId, source);
      }
      case "js" -> {
        return runJavaScriptCode(projectId, source);
      }
      default -> {
        throw new IllegalArgumentException("지원하지 않는 언어입니다.");
      }
    }
  }

  private CodeResult runPythonCode(Long projectId, Source source) {
    String[] saveSourceCommand = {"bash" , "-c", "echo '" + source.getSourceCode() + "' > main.py"};
    String[] runCommand = {"bash", "-c", "python3 main.py"};// 컴파일 명령

    String containerId = projectRepository.findById(projectId)
        .orElseThrow()
        .getContainer()
        .getId();

    CodeResult saveCodeResult = execCommand(containerId, saveSourceCommand);
    CodeResult runCodeResult = execCommand(containerId, runCommand);

    return runCodeResult;
  }

  public CodeResult runJavaCode(Long projectId, Source source) {
    String[] saveSourceCommand = {"bash", "-c", "echo '" + source.getSourceCode() + "' > Main.java"};
    String[] compileCommand = {"bash", "-c", "javac Main.java"};
    String[] runCommand = {"bash", "-c", "java Main"};

    String containerId = projectRepository.findById(projectId)
        .orElseThrow()
        .getContainer()
        .getId();

    CodeResult saveCodeResult = execCommand(containerId, saveSourceCommand);
    CodeResult compileCodeResult = execCommand(containerId, compileCommand);
    CodeResult runCodeResult = execCommand(containerId, runCommand);

    return runCodeResult;
  }

  public CodeResult runJavaScriptCode(Long projectId, Source source) {
    String[] saveSourceCommand = {"bash", "-c", "echo '" + source.getSourceCode() + "' > main.js"};
    String[] runCommand = {"bash", "-c", "node main.js"};

    String containerId = projectRepository.findById(projectId)
        .orElseThrow()
        .getContainer()
        .getId();

    CodeResult saveCodeResult = execCommand(containerId, saveSourceCommand);
    CodeResult runCodeResult = execCommand(containerId, runCommand);

    return runCodeResult;
  }

  private CodeResult execCommand(String containerId, String[] command) {
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
              String output = new String(f.getPayload(), StandardCharsets.UTF_8);
              switch (f.getStreamType()) {
                case STDOUT -> codeResult.appendStandardOutput(output);
                case STDERR -> codeResult.appendStandardError(output);
                default -> {
                  log.error("Unknown stream type: {}", f.getStreamType());
                  throw new IllegalStateException("Unknown stream type" + f.getStreamType().name());
                }
              }
            }
          }
      ).awaitCompletion(60, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error("Interrupted while running code", e);
    }

    return codeResult;
  }

  public void cleanContainer(String containerId) {
    try {
      log.info("Cleaning up container...");
      dockerClient.removeContainerCmd(containerId).withForce(true).exec();
    } catch (Exception e) {
      log.error("Failed to remove container: {}", containerId, e);
    }
  }

//  @Profile("local")
//  @Transactional
//  @PreDestroy
//  public void cleanup() {
//    log.info("Cleaning up containers...");
//    containerRepository.findAll().forEach(container -> {
//      try {
//        dockerClient.removeContainerCmd(container.getId()).withForce(true).exec();
//        userProjectRepository.deleteByProjectId(projectRepository.findByContainerId(container.getId()).getId());
//        log.info("Removed container: {}", container.getId());
//      } catch (Exception e) {
//        log.error("Failed to remove container: {}", container.getId(), e);
//      }
//    });
//  }
}



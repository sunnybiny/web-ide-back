package org.goorm.webide;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.domain.*;
import org.goorm.webide.repository.ChatMessageRepository;
import org.goorm.webide.repository.MeetingRepository;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.goorm.webide.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDB {

  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.initDB();
  }

  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final MeetingRepository meetingRepository;
    private final ChatMessageRepository chatMessageRepository;

    public void initDB() {
      User user1 = new User();
      user1.setName("user1");
      user1.setEmail("test@test.com");
      user1.setPassword("hello1");

      User user2 = new User();
      user2.setName("user2");
      user2.setEmail("test2@test.com");
      user2.setPassword("hello2");

      userRepository.saveAll(List.of(user1, user2));

      Project project = new Project();
      project.setName("my project");
      project.setDescription("demo");
      project.setCreatedBy(user1);

      projectRepository.save(project);

      UserProject userProject1 = new UserProject();
      userProject1.setUser(user1);
      userProject1.setProject(project);
      userProject1.setRole(ProjectRole.MEMBER);

      UserProject userProject2 = new UserProject();
      userProject2.setUser(user2);
      userProject2.setProject(project);
      userProject2.setRole(ProjectRole.LEADER);

      userProjectRepository.saveAll(List.of(userProject1, userProject2));

    }
  }
}
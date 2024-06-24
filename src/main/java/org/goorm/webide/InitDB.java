package org.goorm.webide;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.domain.Project;
import org.goorm.webide.domain.ProjectRole;
import org.goorm.webide.domain.User;
import org.goorm.webide.domain.UserProject;
import org.goorm.webide.domain.UserRole;
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

      Project project1 = new Project();
      project1.setName("my project");
      project1.setDescription("demo");
      project1.setCreatedBy(user1);

      Project project2 = new Project();
      project2.setName("webide");
      project2.setDescription("test");
      project2.setCreatedBy(user1);

      projectRepository.saveAll(List.of(project1, project2));

      UserProject userProject1 = new UserProject();
      userProject1.setUser(user1);
      userProject1.setProject(project1);
      userProject1.setProjectRole(ProjectRole.LEADER);

      UserProject userProject2 = new UserProject();
      userProject2.setUser(user2);
      userProject2.setProject(project1);
      userProject2.setProjectRole(ProjectRole.MEMBER);

      UserProject userProject3 = new UserProject();
      userProject3.setUser(user1);
      userProject3.setProject(project2);
      userProject3.setProjectRole(ProjectRole.LEADER);

      userProjectRepository.saveAll(List.of(userProject1, userProject2, userProject3));

    }
  }
}
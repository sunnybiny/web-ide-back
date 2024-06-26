package org.goorm.webide;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.domain.Meeting;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public void initDB() {
      User user1 = new User();
      user1.setName("유저A");
      user1.setEmail("test@test.com");
      user1.setPassword(passwordEncoder.encode("123123"));
      user1.setUserRole(UserRole.USER);

      User user2 = new User();
      user2.setName("유저B");
      user2.setEmail("test2@test.com");
      user2.setPassword(passwordEncoder.encode("123123"));
      user2.setUserRole(UserRole.USER);

      User user3 = new User();
      user3.setName("유저C");
      user3.setEmail("test3@test.com");
      user3.setPassword(passwordEncoder.encode("123123"));
      user3.setUserRole(UserRole.USER);

      userRepository.saveAll(List.of(user1, user2, user3));

      Project project1 = new Project();
        project1.setName("Python 프로젝트");
      project1.setDescription("webide");
      project1.setCreatedBy(user1);

      Project project2 = new Project();
      project2.setName("데모 프로젝트");
      project2.setDescription("demo");
      project2.setCreatedBy(user1);

      Project project3 = new Project();
      project3.setName("프로젝트 12345");
      project3.setDescription("test");
      project3.setCreatedBy(user1);

      Project project4 = new Project();
      project4.setName("프로젝트 321");
      project4.setDescription("test");
      project4.setCreatedBy(user2);

      Project project5 = new Project();
      project5.setName("프로젝트 234");
      project5.setDescription("test");
      project5.setCreatedBy(user2);

      projectRepository.saveAll(List.of(project1, project2, project3, project4, project5));

      UserProject userProject1 = new UserProject();
      userProject1.setUser(user1);
      userProject1.setProject(project1);
      userProject1.setProjectRole(ProjectRole.LEADER);

      UserProject userProject2 = new UserProject();
      userProject2.setUser(user1);
      userProject2.setProject(project2);
      userProject2.setProjectRole(ProjectRole.LEADER);

      UserProject userProject3 = new UserProject();
      userProject3.setUser(user1);
      userProject3.setProject(project3);
      userProject3.setProjectRole(ProjectRole.LEADER);

      UserProject userProject4 = new UserProject();
      userProject4.setUser(user2);
      userProject4.setProject(project1);
      userProject4.setProjectRole(ProjectRole.LEADER);

      UserProject userProject5 = new UserProject();
      userProject5.setUser(user2);
      userProject5.setProject(project4);
      userProject5.setProjectRole(ProjectRole.LEADER);

      UserProject userProject6 = new UserProject();
      userProject6.setUser(user2);
      userProject6.setProject(project5);
      userProject2.setProjectRole(ProjectRole.LEADER);

      UserProject userProject7 = new UserProject();
      userProject7.setUser(user3);
      userProject7.setProject(project1);
      userProject7.setProjectRole(ProjectRole.LEADER);

      userProjectRepository.saveAll(List.of(userProject1, userProject2, userProject3, userProject4, userProject5, userProject6, userProject7));

      Meeting meeting = new Meeting("신기능 개발 회의", "설명");
      meeting.setProject(project1);
      meetingRepository.save(meeting);

    }
  }
}
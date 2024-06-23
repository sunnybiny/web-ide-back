package org.goorm.webide.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.Project;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.UserUpdateRequestDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.goorm.webide.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public User find(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    public User create(String username, String email, String password) {
        validateUserUniqueness(username, email);

        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserUpdateRequestDto request) {
        // 나중에 닉네임이나 이런식으로 변경
        User user = userRepository.findById(id).orElseThrow();

        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();

        if (StringUtils.hasText(username)) {
            if (userRepository.findByName(username).isPresent()) {
                throw new IllegalArgumentException("해당 이름의 사용자가 이미 있습니다.");
            }
            user.setName(username);
        }
        if (StringUtils.hasText(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("해당 이메일의 사용자가 이미 있습니다");
            }
            user.setEmail(email);
        }
        if (StringUtils.hasText(password)) {
            user.setPassword(password);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<ProjectOverviewDto> findAllProjectsByUserId(Long userId) {
        List<Project> projects = projectRepository.findAllByUserId(userId);
        return projects
            .stream()
            .map(ProjectOverviewDto::new)
            .toList();
    }

    private void validateUserUniqueness(String username, String email) {
        if (userRepository.findByName(username).isPresent()) {
            throw new IllegalArgumentException("해당 이름의 사용자가 이미 있습니다.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("해당 이메일의 사용자가 이미 있습니다");
        }
    }
}

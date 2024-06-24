package org.goorm.webide.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.goorm.webide.auth.JwtUtil;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.Project;
import org.goorm.webide.domain.User;
import org.goorm.webide.domain.UserRole;
import org.goorm.webide.dto.requestDto.UserUpdateRequestDto;
import org.goorm.webide.dto.responseDto.UserLoginResponseDto;
import org.goorm.webide.dto.responseDto.UserSignupResponseDto;
import org.goorm.webide.dto.responseDto.UserUpdateResponseDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
import org.goorm.webide.repository.ProjectRepository;
import org.goorm.webide.repository.UserProjectRepository;
import org.goorm.webide.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService, LogoutHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;

    public User find(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public User loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user;
    }

    @Transactional
    public UserLoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.genearteRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        user.setIsRefreshTokenExpired(false);
        userRepository.save(user);

        UserLoginResponseDto dto = new UserLoginResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            accessToken,
            refreshToken,
            user.getUserRole().name()
        );

        return dto;
    }

    @Transactional
    public UserSignupResponseDto signUp(String username, String email, String password) {
        validateUserUniqueness(username, email);
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(UserRole.USER);
        userRepository.save(user);

        return new UserSignupResponseDto(
            user.getId(),
            user.getName(),
            password,
            user.getEmail(),
            user.getCreatedAt()
        );
    }

    @Transactional
    public UserUpdateResponseDto update(Long id, UserUpdateRequestDto request) {
        // 나중에 닉네임이나 이런식으로 변경
        User user = userRepository.findById(id).orElseThrow();

        String username = request.getUsername();
        String email = request.getEmail();
        String password = passwordEncoder.encode(request.getPassword());

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
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            user.setPassword(password);
        }

        userRepository.save(user);

        return new UserUpdateResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            request.getPassword()
            );
    }

    @Transactional
    public void delete(Long id) {userRepository.deleteById(id);
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

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) {

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            userRepository.findByEmail(jwtUtil.extractUserEmail(token))
                .ifPresent(user -> {
                    user.setIsRefreshTokenExpired(true);
                    userRepository.save(user);
                    });
        }else {
            throw new IllegalStateException("토큰이 없습니다.");
        }
    }
}

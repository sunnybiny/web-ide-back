package org.goorm.webide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goorm.webide.domain.User;
import org.goorm.webide.model.requestDto.UserUpdateRequestDto;
import org.goorm.webide.repository.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User find(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User create(String username, String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public User update(Long id, UserUpdateRequestDto request) {
        // 나중에 닉네임이나 이런식으로 변경
        User user = userRepository.findById(id).orElseThrow();
        user.setName(request.getUsername());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}

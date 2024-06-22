package org.goorm.webide.controller;

import lombok.RequiredArgsConstructor;
import org.goorm.webide.api.API;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.UserCreateRequestDto;
import org.goorm.webide.dto.requestDto.UserUpdateRequestDto;
import org.goorm.webide.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public API<User> find(@PathVariable Long id) {
        User user = userService.find(id);
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PostMapping
    public API<User> create(@RequestBody UserCreateRequestDto request){
        User user = userService.create(request.getUsername(), request.getPassword());
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PatchMapping("/{userId}")
    public API<User> update(@PathVariable Long userId, @RequestBody UserUpdateRequestDto request) {
        User user = userService.update(userId, request);
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @DeleteMapping("/{userId}")
    public API<?> delete(@PathVariable Long userId) {
        userService.delete(userId);
        API<?> api = API.<User>builder()
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }
}

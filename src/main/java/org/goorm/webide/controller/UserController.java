package org.goorm.webide.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.api.API;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.UserCreateRequestDto;
import org.goorm.webide.dto.requestDto.UserUpdateRequestDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
import org.goorm.webide.service.ProjectService;
import org.goorm.webide.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping("/user")
    public API<User> getUser(@RequestParam("userId") Long userId) {
        User user = userService.find(userId);
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PostMapping("/users")
    public API<User> createUser(@RequestBody @Validated UserCreateRequestDto request){
        User user = userService.create(request.getUsername(), request.getEmail(), request.getPassword());
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PatchMapping("/user")
    public API<User> updateUser(@RequestParam("userId") Long userId, @RequestBody UserUpdateRequestDto request) {
        User user = userService.update(userId, request);
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @DeleteMapping("/user")
    public API<?> deleteUser(@RequestParam("userId") Long userId) {
        userService.delete(userId);
        API<?> api = API.<User>builder()
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @GetMapping("/user/projects")
    public API<List<ProjectOverviewDto>> getUserProjects(@RequestParam("userId") Long userId) {
        List<ProjectOverviewDto> projects = userService.findAllProjectsByUserId(userId);
        API<List<ProjectOverviewDto>> api = API.<List<ProjectOverviewDto>>builder()
            .data(projects)
            .resultCode(HttpStatus.OK.toString())
            .resultMessage(HttpStatus.OK.getReasonPhrase())
            .build();

        return api;
    }
}

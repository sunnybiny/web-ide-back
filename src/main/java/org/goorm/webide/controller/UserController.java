package org.goorm.webide.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.awt.desktop.UserSessionListener;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.api.API;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.UserLoginRequestDto;
import org.goorm.webide.dto.requestDto.UserSignupRequestDto;
import org.goorm.webide.dto.requestDto.UserUpdateRequestDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
import org.goorm.webide.service.ProjectService;
import org.goorm.webide.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;

    public API<User> find(@PathVariable Long id) {
        User user = userService.find(id);
        API<User> api = API.<User>builder()
                .data(user)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @PostMapping(("/sign-up"))
    public API<UserSignupResponseDto> signUp(@RequestBody @Validated UserSignupRequestDto request){
        UserSignupResponseDto userSignupResponseDto = userService.signUp(request.getUsername(), request.getEmail(), request.getPassword());
        API<UserSignupResponseDto> api = API.<UserSignupResponseDto>builder()
                .data(userSignupResponseDto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }


    @PostMapping("/login")
    public API<UserLoginResponseDto> login(@RequestBody @Validated UserLoginRequestDto request) {
        UserLoginResponseDto dto = userService.login(request.getEmail(), request.getPassword());
        API<UserLoginResponseDto> api = API.<UserLoginResponseDto>builder()
                .data(dto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }


    @PatchMapping("api/users/{userId}")
    public API<UserUpdateResponseDto> update(@PathVariable Long userId, @RequestBody UserUpdateRequestDto request) {
        UserUpdateResponseDto dto = userService.update(userId, request);
        API<UserUpdateResponseDto> api = API.<UserUpdateResponseDto>builder()
                .data(dto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<API<RuntimeException>>handleIllegalArgumentException(IllegalArgumentException e) {
        API<RuntimeException> api = API.<RuntimeException>builder()
                .resultCode(HttpStatus.BAD_REQUEST.toString())
                .resultMessage(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
    }

    @DeleteMapping("api/users/{userId}")
    public API<?> delete(@PathVariable Long userId) {
        userService.delete(userId);
        API<?> api = API.<User>builder()
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();

        return api;
    }

    @GetMapping
    public API<List<ProjectOverviewDto>> getUserProjects(@RequestParam("userId") Long userId) {
        List<ProjectOverviewDto> projects = projectService.findAll(userId);
        API<List<ProjectOverviewDto>> api = API.<List<ProjectOverviewDto>>builder()
            .data(projects)
            .resultCode(HttpStatus.OK.toString())
            .resultMessage(HttpStatus.OK.getReasonPhrase())
            .build();

        return api;
    }
}

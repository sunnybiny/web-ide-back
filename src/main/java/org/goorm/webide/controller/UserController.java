package org.goorm.webide.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goorm.webide.api.API;
import org.goorm.webide.domain.User;
import org.goorm.webide.dto.requestDto.UserLoginRequestDto;
import org.goorm.webide.dto.requestDto.UserSignupRequestDto;
import org.goorm.webide.dto.requestDto.UserUpdateRequestDto;
import org.goorm.webide.dto.responseDto.ProjectOverviewDto;
import org.goorm.webide.dto.responseDto.UserDto;
import org.goorm.webide.dto.responseDto.UserLoginResponseDto;
import org.goorm.webide.dto.responseDto.UserSignupResponseDto;
import org.goorm.webide.dto.responseDto.UserUpdateResponseDto;
import org.goorm.webide.service.ProjectService;
import org.goorm.webide.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping("/user")
    public API<UserDto> find(@AuthenticationPrincipal User user) {
        UserDto userDto = new UserDto(user);
        API<UserDto> api = API.<UserDto>builder()
                .data(userDto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();
        return api;
    }

    @PostMapping("/sign-up")
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


    @PatchMapping("/user")
    public API<UserUpdateResponseDto> update(@AuthenticationPrincipal User user, @RequestBody UserUpdateRequestDto request) {
        UserUpdateResponseDto dto = userService.update(user.getId(), request);
        API<UserUpdateResponseDto> api = API.<UserUpdateResponseDto>builder()
                .data(dto)
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();
        return api;
    }

    @DeleteMapping("/user")
    public API<?> delete(@AuthenticationPrincipal User user) {
        userService.delete(user.getId());
        API<?> api = API.<User>builder()
                .resultCode(HttpStatus.OK.toString())
                .resultMessage(HttpStatus.OK.getReasonPhrase())
                .build();
        return api;
    }

    @GetMapping("/user/projects")
    public API<List<ProjectOverviewDto>> getUserProjects(@AuthenticationPrincipal User user){
        List<ProjectOverviewDto> projects = projectService.findAll(user);
        API<List<ProjectOverviewDto>> api = API.<List<ProjectOverviewDto>>builder()
            .data(projects)
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
}

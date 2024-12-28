package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.TestService;
import com.sparta.fritown.domain.service.UserService;
import com.sparta.fritown.global.docs.UserControllerDocs;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserController implements UserControllerDocs {

    private final TestService testService;
    private final UserService userService;

    public UserController(TestService testService,UserService userService) {
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping("/health/login/success")
    public String loginSuccess(@RequestParam("accessToken") String accessToken) {
        // 로그로 토큰 확인
        log.info("Login successful. AccessToken: {}", accessToken);

        // 토큰 화면에 표시
        return "<html><body>" +
                "<h1>Login Successful</h1>" +
                "<p>Your Access Token:</p>" +
                "<textarea readonly style='width: 100%; height: 100px;'>" + accessToken + "</textarea>" +
                "</body></html>";
    }

    @GetMapping("/health/login/failure")
    public String failureHealthCheck() {
        return "MyAuthentication Failed; sign up page should be shown";
    }

    @GetMapping("/health/failure")
    public String errorHealthCheck() {
        return "OAuth just failed";
    }


    @PostMapping("/health/new/user/check")
    public String newUser(){
        User user = new User("20@nav", "hihi", "naver");
        return user.getProfileImg();
    }

    @Override
    @GetMapping("/user/recommendation")
    public ResponseDto<List<OpponentDto>> getRecommendedOpponents(@AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        Long userId = userDetails.getId();
        List<OpponentDto> opponents = userService.getRandomUsers(userId);
        return ResponseDto.success(SuccessCode.OK, opponents);
    }


}

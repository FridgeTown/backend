package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.TestService;
import com.sparta.fritown.global.docs.UserControllerDocs;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/health")
public class UserController implements UserControllerDocs {

    private final TestService testService;

    public UserController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/login/success")
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

    @GetMapping("/login/failure")
    public String failureHealthCheck() {
        return "MyAuthentication Failed; sign up page should be shown";
    }

    @GetMapping("/failure")
    public String errorHealthCheck() {
        return "OAuth just failed";
    }


    @PostMapping("/new/user/check")
    public String newUser(){
        User user = new User("20@nav", "hihi", "naver");
        return user.getProfileImg();
    }

}

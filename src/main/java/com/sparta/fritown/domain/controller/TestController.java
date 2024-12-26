package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.service.TestService;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.global.docs.TestControllerDocs;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
@Slf4j
public class TestController implements TestControllerDocs {

    private final TestService testService;

    public TestController(TestService testService) {
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

    @GetMapping("/check")
    public String healthCheck() {
        testService.healthCheck();
        throw ServiceException.of(ErrorCode.USER_NOT_ACCEPTABLE);
    }

    @PostMapping("/new/user/check")
    public String newUser(){
        User user = new User("20@nav", "hihi", "naver");
        return user.getProfileImg();
    }
}

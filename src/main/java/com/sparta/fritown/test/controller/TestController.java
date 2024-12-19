package com.sparta.fritown.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Slf4j
public class TestController {

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
}

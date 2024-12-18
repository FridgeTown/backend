package com.sparta.fritown.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class TestController {

    @GetMapping("/login/success")
    public String successHealthCheck() {
        return "MyAuthentication Success";
    }

    @GetMapping("/login/failure")
    public String failureHealthCheck() {
        return "MyAuthentication Failed; sign up page should be shown";
    }
}

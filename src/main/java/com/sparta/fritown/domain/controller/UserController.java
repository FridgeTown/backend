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
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.s3.service.S3Service;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class UserController implements UserControllerDocs {

    private final TestService testService;
    private final UserService userService;
    private final S3Service s3Service;

    public UserController(TestService testService, UserService userService, S3Service s3Service) {
        this.testService = testService;
        this.userService = userService;
        this.s3Service = s3Service;
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

    @PostMapping("/user/image")
    public ResponseDto<Void> updateProfileImg(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("file")MultipartFile file) {
        try {
            String imageFileName = s3Service.uploadFile(file, userDetails.getId());

            userService.updateProfileImage(userDetails.getId(), imageFileName);
            return ResponseDto.success(SuccessCode.IMAGE_UPLOADED);
        } catch (Exception e) {
            throw ServiceException.of(ErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }


}

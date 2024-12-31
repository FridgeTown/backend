package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.domain.dto.user.UserInfoResponseDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.TestService;
import com.sparta.fritown.domain.service.UserService;
import com.sparta.fritown.global.docs.AuthControllerDocs;
import com.sparta.fritown.global.docs.UserControllerDocs;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.s3.service.S3Service;
import com.sparta.fritown.global.security.controller.AuthController;
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
    private final AuthController authController;

    public UserController(TestService testService, UserService userService, S3Service s3Service, AuthController authController) {
        this.testService = testService;
        this.userService = userService;
        this.s3Service = s3Service;
        this.authController = authController;
    }

    @Override
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

    @Override
    @GetMapping("/health/login/failure")
    public String failureHealthCheck() {
        return "MyAuthentication Failed; sign up page should be shown";
    }

    @Override
    @GetMapping("/health/failure")
    public String errorHealthCheck() {
        return "OAuth just failed";
    }

    @Override
    @GetMapping("/user/recommendation")
    public ResponseDto<List<OpponentDto>> getRecommendedOpponents(@AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        Long userId = userDetails.getId();
        List<OpponentDto> opponents = userService.getRandomUsers(userId);
        return ResponseDto.success(SuccessCode.OK, opponents);
    }

    @Override
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

    @Override
    @GetMapping("/user/info")
    public ResponseDto<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 유저 정보, 채팅 토큰 정보 수집
        Long userId = userDetails.getId();
        User user = userService.getUserInfo(userId);
        String chatToken = authController.callChatLoginApi(userId);

        // DTO- 정보 담기
        UserInfoResponseDto responseDto = new UserInfoResponseDto(user, chatToken);
        return ResponseDto.success(SuccessCode.OK, responseDto);
    }

    @Override
    @GetMapping("/user/{userId}")
    public ResponseDto<UserInfoResponseDto> getUserInfoByUserId(@PathVariable Long userId)
    {
        User user = userService.getUserInfo(userId);
        String chatToken = authController.callChatLoginApi(userId);

        UserInfoResponseDto responseDto = new UserInfoResponseDto(user, chatToken);
        return ResponseDto.success(SuccessCode.OK, responseDto);
    }


}

package com.sparta.fritown.domain.controller;

import com.sparta.fritown.global.docs.TestControllerDocs;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.exception.custom.UserDetailsImpl;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController implements TestControllerDocs {

    @GetMapping("/error")
    public String errorHealthCheck() {
        throw ServiceException.of(ErrorCode.USER_NOT_ACCEPTABLE);
    }

    @GetMapping("/success")
    public ResponseDto<Void> successHealthCheck() {
        return ResponseDto.success(SuccessCode.OK);
    }

    @GetMapping("/success/auth")
    public ResponseDto<Long> successAuthCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        return ResponseDto.success(SuccessCode.OK, userId);

    }
}

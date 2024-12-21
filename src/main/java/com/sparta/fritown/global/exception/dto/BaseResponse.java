package com.sparta.fritown.global.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@Getter
@RequiredArgsConstructor
public abstract class BaseResponse {
    // abstract, 추상 클래스 선언함으로서 단독적으로 사용되지 않도록 함. 주로, ErrorResponseDto || ResponseDto에서 사용될 것.
    private final int status;
    private final String message;
}

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
    /* 목적: 코드 흐름에서 벗어나서 throw error를 할 때, 사용하고자 하기 위함.
     * 사용 위치: 본래, service class에서 throw를 하는 것이 일반적임. 그리고 그것을 권장함.
     * 글자를 그대로 적어서 하드 코딩하는 것을 방지하고자 ErrorCodeEnum을 만듦.
     * 만약, ex) 해당 유저가 없습니다.''' 와 같은 에러를 던지고자 할 땐, ErrorCode class 안에다가 enum하나 추가해주어야 함.
     * IO_EXCEPTION(HttpStatus.BAD_REQUEST, "E001", "IO error"), -> 이런식으로 추가해 주면 됨. E001의 경우엔, 이전 코드가 몇번 인지 확인 후, +1을 해서 적으면 됨.
     * 가장 최근 생성된 errorcode 가 E002라면, 내가 생성할 에러의 코드는 E003이 될 것.
     * 참고로, of. 는 encapsulation을 위해서 만듦. (공부하면 좋을 것 같다 ^#^)
     */


    @GetMapping("/success")
    public ResponseDto<Void> successHealthCheck() {
        return ResponseDto.success(SuccessCode.OK);
    }
    /* 목적: 성공적으로 코드 흐름이 완료되었고, 반환할 값이 없을 때 사용.
     * 위와 같이 반환해주면 된다.
     * 반환하고자 하는 메시지는 위에 언급한 방법과 같이, SucessCode에 들어가서 적어주면 된다.
     * CREATED(HttpStatus.CREATED, "C002", "Created successfully"); -> 요런 식으로
     */


    @GetMapping("/success/auth")
    public ResponseDto<Long> successAuthCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        return ResponseDto.success(SuccessCode.OK, userId);
    }
    /* 목적: 성공적으로 코드 흚이 완료되었고, 반환할 값이 있을 때, 사용
     * 첫 번째 인자로는 successCode 종류가 들어가고, 두 번째 인자로 반환하는 데이터가 들어간다.
     * 메시지는 /success에 언급한 방식대로 진행하면 된다.
     */

}

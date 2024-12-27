package com.sparta.fritown.global.exception.dto;

import com.sparta.fritown.global.exception.SuccessCode;
import org.springframework.http.HttpStatus;

public class ResponseDto<T> extends BaseResponse {
    private final T data;

    private ResponseDto(int status, String message, T data) {
        // private로 constructor 관리
        super(status, message);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public static <T> ResponseDto<T> success(String message, T data) {
        // 정적 팩토리 메서드
        // type 매개변수 사용을 통해, 여러 형태의 data type이 이용 가능하도록 함.
        // static 메서드 사용을 통해 특정 인스턴스에 의존하지 않도록 함. 이를 통해 객체를 생성하지 않고도 클래스 이름으로 직접 호출이 가능하도록 하였다.
        return new ResponseDto<>(HttpStatus.OK.value(), message, data);
    }

    public static ResponseDto<Void> success(SuccessCode successCode) {
        return success(successCode.getMessage(), null);
    }


    public static <T> ResponseDto<T> success(SuccessCode successCode, T data) {
        return success(successCode.getMessage(), data);
    }
}

package com.sparta.fritown.global.exception.dto;

public class ResponseDto<T> extends BaseResponse {
    private final T data;

    private ResponseDto(int status, String message, T data) {
        // private로 constructor 관리
        super(status, message);
        this.data = data;
    }

    public static <T> ResponseDto<T> success(String message, T data) {
        // 정적 팩토리 메서드
        // type 매개변수 사용을 통해, 여러 형태의 data type이 이용 가능하도록 함.
        return new ResponseDto<>(200, message, data);
    }

    public static ResponseDto<Void> success(String message) {
        // 정적 팩토리 메서드
        // overloading : data를 전달하지 않고 사용할 경우엔, 이 메서드를 이용해서 반환하도록 함.
        return success(message, null);
    }
}

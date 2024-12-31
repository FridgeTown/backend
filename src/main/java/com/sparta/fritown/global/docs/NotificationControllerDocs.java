package com.sparta.fritown.global.docs;

import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notification", description = "Notification 관련 API")
public interface NotificationControllerDocs {

    @Operation(
            summary = "알림 구독",
            description = "현재 사용자 ID를 기반으로 SSE 연결을 설정하여 알림을 구독합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 연결 성공",
                    content = @Content(mediaType = "text/event-stream", schema = @Schema(implementation = SseEmitter.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 로그인 필요",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    SseEmitter subscribe(UserDetailsImpl userDetails);
}
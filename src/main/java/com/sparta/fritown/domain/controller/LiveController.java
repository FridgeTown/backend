package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.live.LiveResponseDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.LiveService;
import com.sparta.fritown.global.docs.LiveControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/live")
public class LiveController implements LiveControllerDocs {

    private final LiveService liveService;

    public LiveController(LiveService liveService) {
        this.liveService = liveService;
    }


    @PostMapping("/start")
    public void liveStart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        liveService.liveStart();

    }

    @PostMapping("/end/{matchId}")
    public void liveEnd(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        liveService.liveEnd();
    }

    @PostMapping("/watch/start/{matchId}")
    public ResponseDto<Void> liveWatchStart(@PathVariable Long matchId) {
        // Match의 viewNum 을 1 증가시킵니다.
        liveService.liveWatchStart(matchId);
        return ResponseDto.success(SuccessCode.LIVE_WATCH_STARTED);
    }

    @PostMapping("/watch/end/{matchId}")
    public ResponseDto<Void> liveWatchEnd(@PathVariable Long matchId) {
        // Match의 viewNum을 1 감소시킵니다.
        liveService.liveWatchEnd(matchId);
        return ResponseDto.success(SuccessCode.LIVE_WATCH_ENDED);

    }

    @GetMapping("/list")
    public ResponseDto<List<LiveResponseDto>> getLiveList(){
        List<LiveResponseDto> liveResponseDtos = liveService.getLiveList();
        return ResponseDto.success(SuccessCode.LIVE_LIST, liveResponseDtos);
    }
}

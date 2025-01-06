package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.LiveService;
import com.sparta.fritown.global.docs.LiveControllerDocs;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public void liveWatchStart(@PathVariable Long matchId) {
        // Match의 viewNum 을 1 증가시킵니다.
        liveService.liveWatchStart(matchId);
    }

    @PostMapping("/watch/end/{matchId}")
    public void liveWatchEnd(@PathVariable Long matchId) {
        // Match의 viewNum을 1 감소시킵니다.
        liveService.liveWatchEnd(matchId);
    }

    @GetMapping("/list")
    public void getLiveList(){
        liveService.getLiveList();
    }
}

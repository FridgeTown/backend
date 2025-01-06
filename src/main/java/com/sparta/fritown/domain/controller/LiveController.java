package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.LiveService;
import com.sparta.fritown.global.docs.LiveControllerDocs;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void liveWatchStart() {
        liveService.liveWatchStart();
    }

    @PostMapping("/watch/end/{matchId}")
    public void liveWatchEnd() {
        liveService.liveWatchEnd();

    }

    @GetMapping("/list")
    public void getLiveList(){
        liveService.getLiveList();
    }
}

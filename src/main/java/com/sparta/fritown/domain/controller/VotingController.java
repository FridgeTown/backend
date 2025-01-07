package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.service.VotingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/voting")
public class VotingController {
    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/subscribe/{matchId}")
    public SseEmitter subscribe(@PathVariable Long matchId)
    {
        // 특정 matchId에 대해 SSE 연결 생성
        return votingService.subscribe(matchId);
    }

    @PostMapping("/vote")
    public void vote(@RequestParam Long matchId, @RequestParam Long userId)
    {
        votingService.voteForUser(matchId,userId);
    }

}

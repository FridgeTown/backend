package com.sparta.fritown.domain.test.controller;


import com.sparta.fritown.domain.user.dto.RoundDto;
import com.sparta.fritown.domain.user.service.RoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController {
    private final RoundService roundService;

    public MatchController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/{matchId}/round")
    public ResponseEntity<List<RoundDto>> getRoundsByMatchId(@PathVariable Long matchId) {
        List<RoundDto> rounds = roundService.getRoundsByMatchId(matchId);
        return ResponseEntity.ok(rounds);
    }
}

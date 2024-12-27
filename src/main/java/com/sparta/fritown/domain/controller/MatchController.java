package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.domain.service.MatchService;
import com.sparta.fritown.global.docs.MatchControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController implements MatchControllerDocs {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    @GetMapping("/{matchId}/round")
    public ResponseDto<List<RoundsDto>> getRoundsByMatchId(@PathVariable Long matchId) {
        List<RoundsDto> rounds = matchService.getRoundsByMatchId(matchId);
        System.out.println("HelloWorld");
        System.out.println(rounds);
        return ResponseDto.success(SuccessCode.OK, rounds);
    }

}

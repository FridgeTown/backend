package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.vote.SimpleVoteRequestDto;
import com.sparta.fritown.domain.dto.vote.SimpleVoteResponseDto;
import com.sparta.fritown.domain.service.SimpleVotingService;
import com.sparta.fritown.global.docs.SimpleVotingControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simple")
public class SimpleVotingController implements SimpleVotingControllerDocs {
    private final SimpleVotingService simpleVotingService;
    public SimpleVotingController(SimpleVotingService simpleVotingService) {
        this.simpleVotingService = simpleVotingService;
    }

    @Override
    @PostMapping("/vote")
    public void simpleVoting(@RequestBody SimpleVoteRequestDto simpleVoteRequestDto) {
        simpleVotingService.simpleVoting(simpleVoteRequestDto);
    }

    @Override
    @GetMapping("/vote/{channelId}")
    public ResponseDto<SimpleVoteResponseDto> getVoteResult(@PathVariable String channelId) {
        SimpleVoteResponseDto simpleVoteResponseDto = simpleVotingService.getVoteResult(channelId);
        return ResponseDto.success(SuccessCode.VOTE_RESULT, simpleVoteResponseDto);
    }
}

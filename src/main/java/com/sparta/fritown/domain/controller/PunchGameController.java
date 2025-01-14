package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.punchGame.PunchGameEndResponseDto;
import com.sparta.fritown.domain.dto.punchGame.PunchGameStartRequestDto;
import com.sparta.fritown.domain.dto.punchGame.PunchGameStartResponseDto;
import com.sparta.fritown.domain.service.PunchGameService;
import com.sparta.fritown.domain.websocket.handler.SignalingSocketHandler;
import com.sparta.fritown.global.docs.PunchGameControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/punch-game")
public class PunchGameController implements PunchGameControllerDocs {

    private final PunchGameService punchGameService;
    private final SignalingSocketHandler signalingSocketHandler;

    public PunchGameController(PunchGameService punchGameService, SignalingSocketHandler signalingSocketHandler) {
        this.punchGameService = punchGameService;
        this.signalingSocketHandler = signalingSocketHandler;
    }

    @Override
    @PostMapping("/start")
    public ResponseDto<PunchGameStartResponseDto> punchGameStart(
            @RequestBody PunchGameStartRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            )
    {
        PunchGameStartResponseDto responseDto = punchGameService.startPunchGame(requestDto,userDetails.getId());
        return ResponseDto.success(SuccessCode.PUNCH_GAME_STARTED,responseDto);
    }

    @Override
    @GetMapping("/end/{channelId}")
    public ResponseDto<List<PunchGameEndResponseDto>> punchGameEnd(@PathVariable Long channelId) {
        List<PunchGameEndResponseDto> responseDtos = signalingSocketHandler.endPunchGame(channelId);
        return ResponseDto.success(SuccessCode.PUNCH_GAME_ENDED, responseDtos);
    }
}

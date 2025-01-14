package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.punchGame.PunchGameStartRequestDto;
import com.sparta.fritown.domain.dto.punchGame.PunchGameStartResponseDto;
import com.sparta.fritown.domain.service.PunchGameService;
import com.sparta.fritown.global.docs.PunchGameControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/punch-game")
public class PunchGameController implements PunchGameControllerDocs {

    private final PunchGameService punchGameService;

    public PunchGameController(PunchGameService punchGameService) {
        this.punchGameService = punchGameService;
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
}

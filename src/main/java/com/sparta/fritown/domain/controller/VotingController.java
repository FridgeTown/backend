package com.sparta.fritown.domain.controller;

import com.example.grpc.NodeServiceOuterClass;
import com.sparta.fritown.domain.dto.vote.VoteRequestDto;
import com.sparta.fritown.domain.dto.vote.VoteResponseDto;
import com.sparta.fritown.domain.service.VotingService;
//import com.sparta.fritown.global.docs.VotingControllerDocs;
import com.sparta.fritown.global.docs.VotingControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/voting")
public class VotingController implements VotingControllerDocs {
    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @Override
    @GetMapping("/subscribe/{matchId}")
    public SseEmitter subscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        Long userId = userDetails.getId();

        // 유저 ID와 matchId 로 SSE 연결 생성
        return votingService.subscribe(matchId,userId);
    }

    @Override
    @GetMapping("/unsubscribe/{matchId}")
    public ResponseDto<Void> unsubscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        // 인증된 유저 ID 가져오기
        Long userId = userDetails.getId();
        log.info("unsubscribe 요청: matchId = {}, userId = {}",matchId,userId);

        // VotingService 에서 구독 해지 처리
        votingService.unsubscribe(matchId,userId);

        return ResponseDto.success(SuccessCode.UNSUBSCRIBE_SUCCESS);
    }

    @Override
    @PostMapping("/vote")
    public ResponseDto<VoteResponseDto> vote(@RequestBody VoteRequestDto voteRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {

        // DTO 에서 값 추출
        Long matchId = voteRequestDto.getMatchId();
        String playerNickname = voteRequestDto.getPlayerNickname();

        // 투표 수행
        votingService.voteForUser(matchId,playerNickname,userDetails.getId());

        // 응답 DTO 생성
        VoteResponseDto voteResponseDto = new VoteResponseDto(matchId,playerNickname);
        return ResponseDto.success(SuccessCode.VOTE_SUCCESS,voteResponseDto);
    }


    // 게스트용 API
    @Override
    @GetMapping("/guest/id")
    public ResponseDto<String> generateGuestId()
    {
        String guestId = UUID.randomUUID().toString();
        return ResponseDto.success(SuccessCode.GUEST_ID_GENERATED,guestId);
    }

    @Override
    @GetMapping("/guest/subscribe/{matchId}")
    public SseEmitter guestSubscribe(@PathVariable Long matchId,
                                     @RequestParam(value="guestId") String guestId) {
        return votingService.guestSubscribe(matchId,guestId);
    }

    @Override
    @GetMapping("/guest/unsubscribe/{matchId}")
    public ResponseDto<Void> guestUnsubscribe(@PathVariable Long matchId,
                                              @RequestParam(value="guestId") String guestId)
    {
        votingService.guestUnsubscribe(matchId,guestId);
        return ResponseDto.success(SuccessCode.UNSUBSCRIBE_SUCCESS);
    }


    @Override
    @PostMapping("/guest/vote")
    public ResponseDto<VoteResponseDto> guestVote(
            @RequestParam(value="guestId") String guestId,
            @RequestBody VoteRequestDto voteRequestDto)
    {
        Long matchId = voteRequestDto.getMatchId();
        String playerNickname = voteRequestDto.getPlayerNickname();

        // 게스트 투표 수행
        votingService.guestVoteForUser(matchId,playerNickname,guestId);

        // 응답 DTO 생성
        VoteResponseDto voteResponseDto = new VoteResponseDto(matchId,playerNickname);

        // 성공 응답 반환
        return ResponseDto.success(SuccessCode.VOTE_SUCCESS,voteResponseDto);
    }


}

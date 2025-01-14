package com.sparta.fritown.domain.service;


import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.sparta.fritown.domain.dto.punchGame.PunchGameStartRequestDto;
import com.sparta.fritown.domain.dto.punchGame.PunchGameStartResponseDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PunchGameService {

    private final MatchesRepository matchesRepository;

    public PunchGameService(MatchesRepository matchesRepository) {
        this.matchesRepository = matchesRepository;
    }

    @Transactional
    public PunchGameStartResponseDto startPunchGame(PunchGameStartRequestDto requestDto,Long userId)
    {
        // 1. channelId를 기반으로 Match 찾기
        Matches match =  matchesRepository.findByChannelId(requestDto.getChannelId())
                .orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // 2. 상대방 유저 찾기 내 아이디를 기준으로 찾음
        User opponent = match.getOpponent(userId);

        // 3. 상대방 정보를 DTO 로 변환하여 반환
        return new PunchGameStartResponseDto(
                opponent.getId(),
                opponent.getNickname(),             // 닉네임
                opponent.getGender().toString(),   // 성별 (Enum -> String 변환)
                opponent.getAge(),                 // 나이
                opponent.getHeight(),              // 키
                opponent.getWeight(),              // 체중
                opponent.getWeightClass().toString(), // 체급 (Enum -> String 변환)
                opponent.getProfileImg()           // 프로필 이미지
        );
    }
}

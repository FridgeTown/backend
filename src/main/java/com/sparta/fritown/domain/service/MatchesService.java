package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchesService {
    MatchesRepository matchesRepository;

    @Autowired
    public MatchesService(MatchesRepository matchesRepository) {
        this.matchesRepository = matchesRepository;
    }

    public Matches matchAccept(Long matchId, String userId) {
        Matches match = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        validateParticipant(match, userId);

        if (!match.getStatus().equals(Status.PENDING)) {
            throw ServiceException.of(ErrorCode.MATCH_NOT_PENDING);
        }
        match.setStatus(Status.ACCEPTED);

        return matchesRepository.save(match);
    }

    public Matches matchReject(Long matchId, String userId) {
        Matches match = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        validateParticipant(match, userId);

        if (!match.getStatus().equals(Status.PENDING)) {
            throw ServiceException.of(ErrorCode.MATCH_NOT_PENDING);
        }
        match.setStatus(Status.REJECTED);

        return matchesRepository.save(match);
    }

    private void validateParticipant(Matches match, String userId) {
        Long userIdAsLong;

        try {
            userIdAsLong = Long.parseLong(userId); // String → Long 변환
        } catch (NumberFormatException e) {
            throw ServiceException.of(ErrorCode.IO_EXCEPTION); // 잘못된 형식의 ID
        }

        if (userIdAsLong.equals(match.getChallengedTo().getId())) {
            return;
        }
        throw ServiceException.of(ErrorCode.USER_NOT_PARTICIPANT); // 유효하지 않은 사용자
    }
}

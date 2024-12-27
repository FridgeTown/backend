package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
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

    UserService userService;

    MatchesRepository matchesRepository;

    @Autowired
    public MatchesService(MatchesRepository matchesRepository, UserService userService) {
        this.matchesRepository = matchesRepository;
        this.userService = userService;
    }

    public Matches matchAccept(Long matchId, String email) {
        return handleMatch(matchId, email, Status.ACCEPTED);
    }

    public Matches matchReject(Long matchId, String email) {
        return handleMatch(matchId, email, Status.REJECTED);
    }

    private Matches handleMatch(Long matchId, String email, Status status) {
        User user = userService.findByEmail(email);
        Matches match = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        validateUserParticipation(user, match);
        validateMatchStatus(match);

        match.setStatus(status);
        return matchesRepository.save(match);
    }

    private void validateUserParticipation(User user, Matches match) {
        if (user.getId().equals(match.getChallengedTo().getId())) {
            return;
        }
        throw ServiceException.of(ErrorCode.USER_NOT_PARTICIPANT);
    }

    private void validateMatchStatus(Matches match) {
        if (match.getStatus().equals(Status.PENDING)) {
            return;
        }
        throw ServiceException.of(ErrorCode.MATCH_NOT_PENDING);
    }
}

package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.match.MatchFutureDto;
import com.sparta.fritown.domain.dto.match.MatchInfoDto;
import com.sparta.fritown.domain.dto.match.MatchSummaryDto;
import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.Round;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.UserMatch;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.RoundRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserService userService;
    private final RoundRepository roundRepository;
    private final MatchesRepository matchesRepository;
    private final UserRepository userRepository;

    public List<RoundsDto> getRoundsByMatchId(Long matchId, Long userId) {
        Matches match = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));
        List<UserMatch> userMatches = match.getUserMatches();

        User me = userRepository.findById(userId).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));

        List<Round> rounds = null;
        for (UserMatch userMatch : userMatches) {
            if (userMatch.getUser() == me) {
                rounds = userMatch.getRounds();
                break;
            }
        }
        if(rounds == null)
        {
            throw ServiceException.of(ErrorCode.USER_MATCH_NOT_FOUND);
        }


        return rounds.stream()
                .map(round -> new RoundsDto(
                        round.getRoundNum(),
                        round.getKcal(),
                        round.getHeartBeat()
                ))
                .collect(Collectors.toList());
    }

    public List<MatchSummaryDto> getMatchHistory(Long userId) {

        // 현재 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));

        LocalDate todayDate = LocalDate.now();

        return user.getUserMatches().stream()
                .filter(userMatch -> isValidMatch(userMatch, todayDate))
                .map(userMatch -> createMatchSummaryDto(userMatch, user))
                .collect(Collectors.toList());
    }

    private boolean isValidMatch(UserMatch userMatch, LocalDate todayDate) {
        Matches matches = userMatch.getMatches();
        return matches.getDate().isBefore(todayDate) && matches.getStatus().equals(Status.DONE);
    }

    private boolean isFutureMatch(UserMatch userMatch, LocalDate todayDate) {
        Matches matches = userMatch.getMatches();
        return matches.getDate().isAfter(todayDate) && matches.getStatus().equals(Status.ACCEPTED);
    }


    // MatchSummaryDto 생성 메서드
    private MatchSummaryDto createMatchSummaryDto(UserMatch userMatch, User currentUser) {
        Matches matches = userMatch.getMatches();
        List<Round> rounds = userMatch.getRounds();

        // 라운드 데이터 계산
        MatchInfoDto matchInfo = calculateMatchInfo(rounds, matches.getDate());

        // 상대 유저 찾기
        User opponent = getOpponent(matches, currentUser);

        // MatchSummaryDto 생성
        return new MatchSummaryDto(matches.getId(), matchInfo, opponent.getNickname());
    }

    private MatchFutureDto createMatchFutureDto(UserMatch userMatch, User currentUser) {
        Matches matches = userMatch.getMatches();

        // 상대 유저 찾기
        User opponent = getOpponent(matches, currentUser);

        // MatchFutureDto 생성
        return new MatchFutureDto(matches.getId(), opponent.getNickname(), matches.getDate(), matches.getPlace());
    }

    // MatchInfo 계산 로직 분리
    private MatchInfoDto calculateMatchInfo(List<Round> rounds, LocalDate matchDate) {
        int totalKcal = 0;
        int totalHeartBeat = 0;
        int totalPunchNum = 0;
        int roundNums = rounds.size(); // 이걸로 리스트 길이 확인 가능

        for (Round round : rounds) {
            totalKcal += round.getKcal();
            totalHeartBeat += round.getHeartBeat();
            totalPunchNum += round.getPunchNum();
        }

        int avgHeartBeat = roundNums > 0 ? totalHeartBeat / roundNums : 0;
        return new MatchInfoDto(totalKcal, avgHeartBeat, totalPunchNum, roundNums, matchDate);
    }

    // 상대 유저 찾기
    private User getOpponent(Matches matches, User currentUser) {
        return matches.getChallengedBy().equals(currentUser)
                ? matches.getChallengedTo()
                : matches.getChallengedBy();
    }



    public List<MatchFutureDto> getMatchFuture(Long userId) {
        // 현재 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));

        LocalDate todayDate = LocalDate.now();

        return user.getUserMatches().stream()
                .filter(userMatch -> isFutureMatch(userMatch, todayDate))
                .map(userMatch -> createMatchFutureDto(userMatch, user))
                .collect(Collectors.toList());
    }


    public boolean matchAccept(Long matchId, String email) {
        return handleMatch(matchId, email, Status.ACCEPTED);
    }

    public boolean matchReject(Long matchId, String email) {
        return handleMatch(matchId, email, Status.REJECTED);
    }

    private boolean handleMatch(Long matchId, String email, Status status) {
        System.out.println("matchId: " + matchId);
        User user = userService.findByEmail(email);
        Matches matches = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        validateUserParticipation(user, matches);
        validateMatchStatus(matches);

        matches.setStatus(status);
        matchesRepository.save(matches);
        return true;
    }

    private void validateUserParticipation(User user, Matches matches) {
        if (user.getId().equals(matches.getChallengedTo().getId())) {
            return;
        }
        throw ServiceException.of(ErrorCode.USER_NOT_CHALLENGED_TO);
    }

    private void validateMatchStatus(Matches matches) {
        if (matches.getStatus().equals(Status.PENDING)) {
            return;
        }
        throw ServiceException.of(ErrorCode.MATCH_NOT_PENDING);
    }
}


package com.sparta.fritown.domain.service;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
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
import com.sparta.fritown.domain.repository.UserMatchRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final RoundRepository roundRepository;
    private final MatchesRepository matchesRepository;
    private final UserRepository userRepository;
    private final UserMatchRepository userMatchRepository;

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

    @Transactional
    public void requestMatch(Long opponentId, Long userId) {
        //현재 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));

        User opponent = userRepository.findById(opponentId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.USER_OP_NOT_FOUND));

        List<Matches> matches = matchesRepository.findByChallengedToAndChallengedBy(user, opponent);
        List<Matches> requestedMatches = matchesRepository.findByChallengedToAndChallengedBy(opponent, user);

        // 기존에 상대가 스파링 요청을 한 상태일 시, 수락
        for (Matches matched : matches) {
            if (matched.getStatus().equals(Status.PENDING)) {
                matched.setStatus(Status.ACCEPTED);
                // 이후 채팅방 생성 로직이 들어가거나 해야 할 듯.
                return;
            }
        }

        // 이미 스파링 신청을 한 상대일 때,
        List<Status> invalidStatuses = List.of(Status.PENDING, Status.PROGRESS, Status.ACCEPTED);
        for (Matches requestedMatch : requestedMatches) {
            if (invalidStatuses.contains(requestedMatch.getStatus())) {
                return;
            }
        }

        Matches newMatch = new Matches(opponent, user, Status.PENDING);
        UserMatch userMatch = new UserMatch(newMatch, opponent);
        UserMatch opponentMatch = new UserMatch(newMatch, user);

        matchesRepository.save(newMatch);
        userMatchRepository.save(userMatch);
        userMatchRepository.save(opponentMatch);
    }
}


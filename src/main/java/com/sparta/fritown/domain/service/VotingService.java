package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class VotingService {

    /**
     * userEmitters 데이터 구조:
     *
     * Map<Long, Map<String, SseEmitter>>
     * └── matchId (Long) : 특정 매치 ID
     *     └── Map<String, SseEmitter>
     *         └── nickname (String) : 매치에 연결된 특정 유저의 닉네임
     *             └── SseEmitter : 해당 유저와의 SSE 연결 객체
     *
     * 예시 데이터:
     *
     * userEmitters = {
     *     1L: { // matchId 1
     *         "player1": SseEmitter@1234, // 닉네임 "player1"의 SseEmitter
     *         "player2": SseEmitter@5678  // 닉네임 "player2"의 SseEmitter
     *     },
     *     2L: { // matchId 2
     *         "player3": SseEmitter@9101 // 닉네임 "player3"의 SseEmitter
     *     }
     * }
     *
     * 구조 설명:
     * - Top-level Map: matchId를 키로, 각 매치에 연결된 유저와 그들의 SseEmitter를 관리.
     * - Nested Map: nickname을 키로, 각 유저의 SseEmitter를 저장.
     */
    private final Map<Long,Map<Long,SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final Map<Long,Map<Long,Boolean>> voted = new ConcurrentHashMap<>();
    private final Map<Long,Map<String,Integer>> voteResults = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final MatchesRepository matchesRepository;

    public VotingService(UserRepository userRepository,MatchesRepository matchesRepository) {
        this.userRepository = userRepository;
        this.matchesRepository = matchesRepository;
    }

    // Live 생성 혹은 시청을 시작시에 subscribe 가 호출 된다.
    public SseEmitter subscribe(Long matchId, Long userId)
    {
        // 구독을 처음시작할 때에는 투표 여부를 기본값으로 false로 둔다.
        voted.computeIfAbsent(matchId, key -> new ConcurrentHashMap<>()).put(userId, false);

        // matchId에 해당하는 Matches 엔티티를 확인
        Matches match = matchesRepository.findById(matchId).orElseThrow(()-> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // 새로운 SseEmitter 생성
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // matchId와 userId를 기준으로 SseEmitter 저장
        userEmitters.computeIfAbsent(matchId, key -> new ConcurrentHashMap<>()).put(userId, emitter);

        // 연결 종료, 타임 아웃, 오류 발생 시 연결 해제
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: matchId {}, emitter {}", matchId, emitter);
            removeEmitter(matchId, userId);
        });
        emitter.onTimeout(() -> {
            log.info("SSE 연결 종료: matchId {}, emitter {}", matchId, emitter);
            removeEmitter(matchId, userId);
        });
        emitter.onError((e) -> {
            log.info("SSE 연결 종료: matchId {}, emitter {}", matchId, emitter);
            removeEmitter(matchId, userId);
        });

        try {
            // 클라이언트에 연결 성공 메시지 전송
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공! matchId: " + matchId));
            log.info("SSE 연결 성공: matchId {}, emitter {}", matchId, emitter);
        } catch (Exception e) {
            // 전송 중 오류가 발생하면 emitter를 제거
            removeEmitter(matchId, userId);
            log.error("SSE 연결 중 오류 발생: matchId {}, emitter {}", matchId, emitter, e);
        }

        return emitter;
    }

    public void unsubscribe(Long matchId, Long userId)
    {
        // matchId에 해당하는 Matches 엔티티를 확인
        Matches match = matchesRepository.findById(matchId).orElseThrow(()-> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // matchId에 연결된 emitters 리스트 가져오기
        Map<Long,SseEmitter> userSseMap = userEmitters.get(matchId);

        if(userSseMap != null)
        {
            SseEmitter emitter = userSseMap.remove(userId);
            log.info("Emitter 제거됨: matchId={}, userId={}, emitter={}", matchId, userId, emitter);
            // userId 에 연결된 모든 emitter가 제거되면 matchId 자체를 제거
            if(userSseMap.isEmpty())
            {
                userEmitters.remove(matchId);
                log.info("모든 Emitters가 제거됨: matchId = {}",matchId);
            }
        } else {
            log.warn("해당 matchId에 대한 SseEmitter가 없습니다: matchId={}, userId={}",matchId,userId);
        }

    }



    // 특정 match의 특정 userId를 가진 user에게 투표
    public void voteForUser(Long matchId, String playerNickname, Long voterUserId)
    {
        // matchId에 해당하는 Matches 엔티티를 확인
        Matches match = matchesRepository.findById(matchId).orElseThrow(()-> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // 닉네임을 기반으로 userId 검색
        Optional<User> optionalUser = userRepository.findByNickname(playerNickname);
        if(optionalUser.isEmpty())
        {
            log.warn("존재하지 않는 닉네임: nickname={}",playerNickname);
            throw ServiceException.of(ErrorCode.USER_NOT_FOUND);
        }

        User player = optionalUser.get();
        Long playerId = player.getId();

        // playerUserId 가 매치 참여자인지 검증한다.
        match.validateMatchedUserId(playerId);

        // voterUserId 가 이미 투표했는지 확인
        Map<Long,Boolean> voterStatus = voted.computeIfAbsent(matchId,key -> new ConcurrentHashMap<>());

        if(Boolean.TRUE.equals(voterStatus.get(voterUserId))) {
            log.warn("이미 투표한 사용자: matchId={}, userId={}", matchId, voterUserId);
            throw ServiceException.of(ErrorCode.ALREADY_VOTED);
        }

        // matchId에 대한 닉네임별 투표 결과 가져오기 (없으면 생성)
        Map<String,Integer> nicknameVotes = voteResults.computeIfAbsent(matchId,key-> new ConcurrentHashMap<>());

        // 투표 업데이트
        nicknameVotes.put(playerNickname,nicknameVotes.getOrDefault(playerNickname,0) +1);

        // 투표 상태를 true 로 설정
        voterStatus.put(voterUserId, true);
        log.info("투표 업데이트: matchId {}, userId {}, 현재 투표 결과 {}", matchId, playerId, nicknameVotes);

        // 닉네임 기반 투표 결과로 변환하여 프론트로 전달
        broadcastVoteUpdate(matchId, nicknameVotes);
    }




    private void removeEmitter(Long matchId, Long userId) {
        Map<Long,SseEmitter> userSseMap = userEmitters.get(matchId);

        if(userSseMap!=null)
        {
            // userId에 해당하는 emitter 제거
            SseEmitter emitter = userSseMap.remove(userId);
            log.info("Emitter 제거됨: matchId={}, userId={}, emitter={}", matchId, userId, emitter);

            // userId에 연결된 모든 emitter가 제거되면 matchId 자체를 제거
            if(userSseMap.isEmpty())
            {
                userEmitters.remove(matchId);
                log.info("모든 Emitters가 제거됨: matchId={}",matchId);
            } else {
                log.warn("SSE 연결이 없는 matchId 입니다.matchId={}",matchId);
            }

        }
    }

    private void broadcastVoteUpdate(Long matchId, Map<String,Integer> nicknameVotes) {
        Map<Long,SseEmitter> userSseMap= userEmitters.get(matchId);
        if (userSseMap != null) {
            userSseMap.forEach((userId, emitter) -> {
                try {
                    // 이벤트 전송
                    emitter.send(SseEmitter.event()
                            .name("voteUpdate")
                            .data(nicknameVotes));
                    log.info("투표 업데이트 전송 성공: matchId={}, userId={}, votes={}", matchId, userId, nicknameVotes);
                } catch (IOException e) {
                    // 전송 실패 시 Emitter 제거
                    log.error("전송 실패: matchId={}, userId={}, votes={}", matchId, userId, nicknameVotes, e);
                    removeEmitter(matchId, userId);
                }
            });
        } else {
            log.warn("SSE 연결이 없는 matchId 입니다: matchId={}", matchId);
        }
    }

}

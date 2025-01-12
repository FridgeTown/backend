package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import jakarta.transaction.Transactional;
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
    // User
    private final Map<Long,Map<Long,SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final Map<Long,Map<Long,Boolean>> voted = new ConcurrentHashMap<>();

    /**
     * guestEmitters 데이터 구조:
     *
     * Map<Long, Map<String, SseEmitter>>
     * └── matchId (Long) : 특정 매치 ID
     *     └── Map<String, SseEmitter>
     *         └── guestId (String, UUID) : 특정 게스트의 고유 ID(UUID 형식)
     *             └── SseEmitter : 해당 게스트와의 SSE 연결 객체
     *
     * 예시 데이터:
     *
     * guestEmitters = {
     *     1L: { // matchId 1
     *         "550e8400-e29b-41d4-a716-446655440000": SseEmitter@1234, // 게스트 "550e8400-e29b-41d4-a716-446655440000"의 SseEmitter
     *         "123e4567-e89b-12d3-a456-426614174001": SseEmitter@5678  // 게스트 "123e4567-e89b-12d3-a456-426614174001"의 SseEmitter
     *     },
     *     2L: { // matchId 2
     *         "789e1234-e56b-78c9-d012-345678901234": SseEmitter@9101  // 게스트 "789e1234-e56b-78c9-d012-345678901234"의 SseEmitter
     *     }
     * }
     *
     * 구조 설명:
     * - Top-level Map: matchId를 키로, 각 매치에 연결된 게스트와 그들의 SseEmitter를 관리.
     * - Nested Map: guestId(UUID 형식)를 키로, 각 게스트의 SseEmitter를 저장.
     * - SseEmitter: 게스트와 서버 간 SSE 연결 객체로, 클라이언트에게 실시간 메시지를 전송.
     *
     * 데이터 흐름:
     * 1. 게스트가 특정 매치(matchId)에 연결 요청을 보내면 UUID를 생성하여 guestId로 사용.
     * 2. 생성된 guestId와 SseEmitter를 guestEmitters에 저장.
     * 3. 매치 ID와 guestId를 기반으로 실시간 메시지 전송 및 연결 관리.
     */

    // Guest
    private final Map<Long, Map<String, SseEmitter>> guestEmitters = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Boolean>> guestVoted = new ConcurrentHashMap<>();

    // 투표 결과
    private final Map<Long,Map<String,Integer>> voteResults = new ConcurrentHashMap<>();


    private final UserRepository userRepository;
    private final MatchesRepository matchesRepository;

    public VotingService(UserRepository userRepository,MatchesRepository matchesRepository) {
        this.userRepository = userRepository;
        this.matchesRepository = matchesRepository;
    }

    /* 유저 */

    // Live 생성 혹은 시청을 시작시에 subscribe 가 호출 된다.
    @Transactional
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
    @Transactional
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



    // 특정 match 의 특정 playerNickname 을 가진 user 에게 투표
    @Transactional
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

    // Live 생성 혹은 시청을 시작할 때 게스트 구독이 호출된다.
    @Transactional
    public SseEmitter guestSubscribe(Long matchId, String guestId) {
        // 구독을 처음 시작할 때에는 투표 여부를 기본값으로 false로 둔다.
        guestVoted.computeIfAbsent(matchId, key -> new ConcurrentHashMap<>()).put(guestId, false);

        // matchId에 해당하는 Matches 엔티티를 확인
        //Matches match = matchesRepository.findById(matchId)
        //        .orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // 새로운 SseEmitter 생성
        SseEmitter emitter = new SseEmitter(60000L);

        // matchId와 guestId를 기준으로 SseEmitter 저장
        guestEmitters.computeIfAbsent(matchId, key -> new ConcurrentHashMap<>()).put(guestId, emitter);

        // 연결 종료, 타임 아웃, 오류 발생 시 연결 해제
        emitter.onCompletion(() -> {
            log.info("게스트 SSE 연결 종료: matchId={}, guestId={}", matchId, guestId);
            removeGuestEmitter(matchId, guestId);
        });
        emitter.onTimeout(() -> {
            log.info("게스트 SSE 연결 타임아웃: matchId={}, guestId={}", matchId, guestId);
            removeGuestEmitter(matchId, guestId);
        });
        emitter.onError((e) -> {
            log.info("게스트 SSE 연결 오류: matchId={}, guestId={}", matchId, guestId);
            removeGuestEmitter(matchId, guestId);
        });

        try {
            // 클라이언트에 연결 성공 메시지 전송
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공! matchId: " + matchId));
            log.info("게스트 SSE 연결 성공: matchId={}, guestId={}", matchId, guestId);
        } catch (IOException e) {
            // 전송 중 오류가 발생하면 emitter를 제거
            removeGuestEmitter(matchId, guestId);
            log.error("게스트 SSE 연결 중 오류 발생: matchId={}, guestId={}", matchId, guestId, e);
        }

        return emitter;
    }

    // 게스트 SSE 구독 해지
    @Transactional
    public void guestUnsubscribe(Long matchId, String guestId) {
        // matchId에 해당하는 Matches 엔티티를 확인
        Matches match = matchesRepository.findById(matchId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // guestEmitters에서 guestId에 해당하는 Emitter 제거
        Map<String, SseEmitter> matchEmitters = guestEmitters.get(matchId);

        if (matchEmitters != null) {
            SseEmitter emitter = matchEmitters.remove(guestId);
            if (emitter != null) {
                log.info("게스트 SSE 구독 해지 성공: matchId={}, guestId={}", matchId, guestId);
            } else {
                log.warn("게스트 SSE 연결이 존재하지 않습니다: matchId={}, guestId={}", matchId, guestId);
            }

            // 해당 matchId의 모든 guestId가 제거되었으면 matchId도 삭제
            if (matchEmitters.isEmpty()) {
                guestEmitters.remove(matchId);
                log.info("모든 게스트 SSE 연결 해제: matchId={}", matchId);
            }
        } else {
            log.warn("해당 matchId에 대한 게스트 SSE 연결이 없습니다: matchId={}", matchId);
        }
    }

    // 특정 match 의 특정 guestId를 가진 게스트에게 투표
    @Transactional
    public void guestVoteForUser(Long matchId, String playerNickname, String guestId) {
        // matchId에 해당하는 Matches 엔티티를 확인
        Matches match = matchesRepository.findById(matchId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        // 닉네임을 기반으로 userId 검색
        Optional<User> optionalUser = userRepository.findByNickname(playerNickname);
        if (optionalUser.isEmpty()) {
            log.warn("존재하지 않는 닉네임: nickname={}", playerNickname);
            throw ServiceException.of(ErrorCode.USER_NOT_FOUND);
        }

        User player = optionalUser.get();
        Long playerId = player.getId();

        // playerUserId가 매치 참여자인지 검증
        match.validateMatchedUserId(playerId);

        // guestId가 이미 투표했는지 확인
        Map<String, Boolean> guestStatus = guestVoted.computeIfAbsent(matchId, key -> new ConcurrentHashMap<>());

        if (Boolean.TRUE.equals(guestStatus.get(guestId))) {
            log.warn("이미 투표한 게스트: matchId={}, guestId={}", matchId, guestId);
            throw ServiceException.of(ErrorCode.ALREADY_VOTED);
        }

        // matchId에 대한 닉네임별 투표 결과 가져오기 (없으면 생성)
        Map<String, Integer> nicknameVotes = voteResults.computeIfAbsent(matchId, key -> new ConcurrentHashMap<>());

        // 투표 업데이트
        nicknameVotes.put(playerNickname, nicknameVotes.getOrDefault(playerNickname, 0) + 1);

        // 투표 상태를 true 로 설정
        guestStatus.put(guestId, true);
        log.info("게스트 투표 업데이트: matchId={}, guestId={}, 현재 투표 결과={}", matchId, guestId, nicknameVotes);

        // 닉네임 기반 투표 결과를 실시간으로 프론트로 전달
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

    private void broadcastVoteUpdate(Long matchId, Map<String, Integer> nicknameVotes) {
        // 유저들에게 메시지 전송
        Map<Long, SseEmitter> userSseMap = userEmitters.get(matchId);
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
                    log.error("유저 전송 실패: matchId={}, userId={}, votes={}", matchId, userId, nicknameVotes, e);
                    removeEmitter(matchId, userId);
                }
            });
        } else {
            log.warn("SSE 연결이 없는 유저: matchId={}", matchId);
        }

        // 게스트들에게 메시지 전송
        Map<String, SseEmitter> guestSseMap = guestEmitters.get(matchId);
        if (guestSseMap != null) {
            guestSseMap.forEach((guestId, emitter) -> {
                try {
                    // 이벤트 전송
                    emitter.send(SseEmitter.event()
                            .name("voteUpdate")
                            .data(nicknameVotes));
                    log.info("투표 업데이트 전송 성공: matchId={}, guestId={}, votes={}", matchId, guestId, nicknameVotes);
                } catch (IOException e) {
                    // 전송 실패 시 Emitter 제거
                    log.error("게스트 전송 실패: matchId={}, guestId={}, votes={}", matchId, guestId, nicknameVotes, e);
                    removeGuestEmitter(matchId, guestId);
                }
            });
        } else {
            log.warn("SSE 연결이 없는 게스트: matchId={}", matchId);
        }
    }


    private void removeGuestEmitter(Long matchId, String guestId) {
        Map<String, SseEmitter> matchEmitters = guestEmitters.get(matchId);

        if (matchEmitters != null) {
            matchEmitters.remove(guestId);
            if (matchEmitters.isEmpty()) {
                guestEmitters.remove(matchId);
                log.info("모든 게스트 SSE 연결 해제: matchId={}", matchId);
            }
        }
    }



}

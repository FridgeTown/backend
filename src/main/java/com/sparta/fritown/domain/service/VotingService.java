package com.sparta.fritown.domain.service;

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

    // matchId를 기준으로 SseEmitter 리스트 관리
    // 하나의 matchId에 여러 클라이언트가 연결되기 때문에 SseEmitter를 리스트로 만듦
    private final Map<Long,List<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();
    // matchId를 기준으로 userId별 투표 수 관리
    private final Map<Long,Map<Long,Integer>> voteResults = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long matchId)
    {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.computeIfAbsent(matchId,key -> new ArrayList<>()).add(emitter);


        // 연결 종료, 타임 아웃, 오류 발생 시 연결 해제
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: matchId {}, emitter {}", matchId, emitter);
            removeEmitter(matchId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SSE 연결 종료: matchId {}, emitter {}", matchId, emitter);
            removeEmitter(matchId, emitter);
        });
        emitter.onError((e) -> {
            log.info("SSE 연결 종료: matchId {}, emitter {}", matchId, emitter);
            removeEmitter(matchId, emitter);
        });

        try {
            // 클라이언트에 연결 성공 메시지 전송
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공! matchId: " + matchId));
            log.info("SSE 연결 성공: matchId {}, emitter {}", matchId, emitter);
        } catch (Exception e) {
            // 전송 중 오류가 발생하면 emitter를 제거
            removeEmitter(matchId, emitter);
            log.error("SSE 연결 중 오류 발생: matchId {}, emitter {}", matchId, emitter, e);
        }

        return emitter;
    }

    // 특정 match의 특정 userId를 가진 user에게 투표
    public void voteForUser(Long matchId, Long userId)
    {
        // matchId에 대한 유저별 투표 결과 가져오기 (없으면 생성)
        Map<Long,Integer> userVotes = voteResults.computeIfAbsent(matchId,key -> new HashMap<>());
        userVotes.put(userId, userVotes.getOrDefault(userId, 0) + 1);

        log.info("투표 업데이트: matchId {}, userId {}, 현재 투표 결과 {}", matchId, userId, userVotes);

        // 현재 matchId에 연결된 모든 클라이언트에게 실시간 데이터 전송
        List<SseEmitter> emitters = sseEmitters.get(matchId);
        if(emitters!=null)
        {
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("voteUpdate")
                            .data(userVotes));
                } catch(IOException e) {
                    emitters.remove(emitter);
                    log.error("SSE 전송 실패: matchId {}",matchId);
                }
            }

            );
        }
    }

    private void removeEmitter(Long matchId, SseEmitter emitter) {
        List<SseEmitter> emitters = sseEmitters.get(matchId); // matchId에 해당하는 SseEmitter 리스트 가져오기
        if (emitters != null) {
            emitters.remove(emitter); // 특정 emitter만 제거
            log.info("Emitter 제거됨: matchId {}, emitter {}", matchId, emitter);

            // 리스트가 비어 있으면 matchId 자체를 제거
            if (emitters.isEmpty()) {
                sseEmitters.remove(matchId);
                log.info("모든 Emitters가 제거됨: matchId {}", matchId);
            }
        }
    }

}

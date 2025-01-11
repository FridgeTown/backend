package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    public NotificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.put(userId, emitter);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onError((e) -> sseEmitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connect"). data("emitter 연결 됨"));
            log.info("emitter 연결이 되었습니다!");
        } catch (IOException e) {
            sseEmitters.remove(userId);
        }

        return emitter;
    }

    public void sendNotification(Long opponentId, String nickname) {
        SseEmitter emitter = sseEmitters.get(opponentId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(nickname+"님이 스파링 요청을 보냈습니다!"));
            } catch (IOException e) {
                sseEmitters.remove(opponentId);
            }
        }
    }

    public void sendMatchCreatedNotification(Long userId, Long opponentId) {
        SseEmitter opEmitter = sseEmitters.get(opponentId);
        SseEmitter userEmitter = sseEmitters.get(userId);

        User opUser = userRepository.findById(opponentId).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));


        sendNotificationByEmitter(opEmitter, opponentId, user.getNickname());
        sendNotificationByEmitter(userEmitter, userId, opUser.getNickname());
    }

    private void sendNotificationByEmitter(SseEmitter emitter, Long userId, String opNickname) {
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(opNickname + "님과 매치가 성사되었습니다!"));
            } catch (IOException e) {
                sseEmitters.remove(userId);
            }
        }
    }
}

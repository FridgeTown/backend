package com.sparta.fritown.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ChatService {


    @Value("${talkplus.api-url.user-create}")
    private String userCreateApiUrl;

    @Value("${talkplus.api-url.push-enable}")
    private String pushEnableApiUrl;

    @Value("${talkplus.api-url.user-create}")
    private String channelCreateApiUrl;

    @Value("${talkplus}.api-url.get-user-channels")
    private String getUserChannelsApiUrl;


    @Value("${talkplus.app-id}")
    private String appId;

    @Value("${talkplus.api-key}")
    private String apiKey;


    private final RestTemplate restTemplate = new RestTemplate();

    public void createUserWithPushNotification(Long userId, String password)
    {
        // 사용자 생성
        createUser(Objects.toString(userId), password);
        // 푸쉬 알림 활성화
        enablePushNotification(Objects.toString(userId));
    }

    public String createUser(String userId, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        // 요청 본문 설정
        Map<String, String> body = new HashMap<>();
        body.put("userId", Objects.toString(userId));
        body.put("password", password);

        // HTTP 요청 생성
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                userCreateApiUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("User created successfully: {}", userId);
            return response.getBody();
        } else {
            log.error("Failed to create user: {}", response.getStatusCode());
            throw new RuntimeException("Failed to create user: " + response.getStatusCode());
        }
    }

    private void enablePushNotification(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String enablePushUrl = pushEnableApiUrl.replace(":userId", userId); // URL에 userId 경로 변수 삽입

        ResponseEntity<String> response = restTemplate.exchange(
                enablePushUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Push notification enabled for user: {}", userId);
        } else {
            log.error("Failed to enable push notification: {}", response.getStatusCode());
            throw new RuntimeException("Failed to enable push notification: " + response.getStatusCode());
        }
    }



    public String createChannel(String channelName, String channelType) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        // 요청 본문 설정
        Map<String, Object> body = new HashMap<>();
        body.put("name", channelName);
        body.put("type", channelType); // 채널 타입 : private, public, invitationOnly

        // HTTP 요청 생성
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                channelCreateApiUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Channel created successfully: " + response.getBody());
            return response.getBody();
        } else {
            System.err.println("Failed to create channel: " + response.getStatusCode());
            throw new RuntimeException("Failed to create channel: " + response.getStatusCode());
        }
    }





}

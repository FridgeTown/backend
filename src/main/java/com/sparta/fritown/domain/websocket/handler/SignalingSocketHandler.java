package com.sparta.fritown.domain.websocket.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingSocketHandler extends TextWebSocketHandler {

    // channelId별로 WebSocketSession을 관리
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> channels = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String channelId = getChannelId(session);
        channels.putIfAbsent(channelId, new ConcurrentHashMap<>());
        channels.get(channelId).put(session.getId(), session);
        System.out.println("Session " + session.getId() + " joined channel: " + channelId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String channelId = getChannelId(session);
        String payload = message.getPayload();

        try {
            // JSON 데이터 파싱
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);

            // JSON 데이터에 필수 키 확인
            if (!data.containsKey("punch") || !data.containsKey("heartRate") ||
                    !data.containsKey("calories") || !data.containsKey("nickname")) {
                throw new IllegalArgumentException("Invalid message format");
            }

            // 전송할 메시지 생성
            String response = objectMapper.writeValueAsString(Map.of(
                    "punch", data.get("punch"),
                    "heartRate", data.get("heartRate"),
                    "calories", data.get("calories"),
                    "nickname", data.get("nickname")
            ));

            // 같은 channelId의 다른 클라이언트에게 메시지 전달
            if (channels.containsKey(channelId)) {
                for (WebSocketSession s : channels.get(channelId).values()) {
                    if (s.isOpen() && !s.getId().equals(session.getId())) { // 자기 자신 제외
                        s.sendMessage(new TextMessage(response));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Invalid message format: " + payload);
            session.sendMessage(new TextMessage("Error: Invalid message format"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String channelId = getChannelId(session);
        if (channels.containsKey(channelId)) {
            channels.get(channelId).remove(session.getId());
            System.out.println("Session " + session.getId() + " left channel: " + channelId);
        }
    }

    private String getChannelId(WebSocketSession session) {
        // URL 경로에서 channelId 추출
        String path = session.getUri().getPath();
        return path.split("/channel/")[1];
    }
}
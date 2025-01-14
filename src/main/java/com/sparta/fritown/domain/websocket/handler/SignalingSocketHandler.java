package com.sparta.fritown.domain.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fritown.domain.dto.punchGame.PunchGameEndResponseDto;
import com.sparta.fritown.domain.websocket.model.UserStats;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> channels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, UserStats>> userStats = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String channelId = getChannelId(session);
        channels.putIfAbsent(channelId, new ConcurrentHashMap<>());
        userStats.putIfAbsent(channelId, new ConcurrentHashMap<>());
        channels.get(channelId).put(session.getId(), session);
        System.out.println("Session " + session.getId() + " joined channel: " + channelId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String channelId = getChannelId(session);
        String payload = message.getPayload();

        try {
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);

            if ("update".equals(data.get("type"))) {
                handleUpdateStats(channelId, session.getId(), data);
                broadcastMessage(channelId, session, payload); // 다른 사용자에게 전달
            } else if ("final".equals(data.get("type"))) {
                sendFinalStats(channelId); // 모든 사용자에게 개별 통계 전송
            } else {
                throw new IllegalArgumentException("Invalid message type");
            }
        } catch (Exception e) {
            System.err.println("Invalid message format: " + payload);
            session.sendMessage(new TextMessage("Error: Invalid message format"));
        }
    }

    private void handleUpdateStats(String channelId, String sessionId, Map<String, Object> data) {
        userStats.putIfAbsent(channelId, new ConcurrentHashMap<>());
        UserStats stats = userStats.get(channelId).getOrDefault(sessionId, new UserStats());

        stats.updateStats(
                (int) data.get("punch"),
                (double) data.get("heartRate"),
                (double) data.get("calories"),
                (String) data.get("nickname")
        );
        userStats.get(channelId).put(sessionId, stats);
        System.out.println("Updated stats for session " + sessionId + ": " + stats);
    }

    private void sendFinalStats(String channelId) throws Exception {
        if (!userStats.containsKey(channelId)) return;

        List<Map<String, Object>> finalStatsList = new ArrayList<>();

        for (Map.Entry<String, UserStats> entry : userStats.get(channelId).entrySet()) {
            UserStats stats = entry.getValue();
            finalStatsList.add(Map.of(
                    "nickname", stats.getNickname(),
                    "finalPunch", stats.getFinalPunch(),
                    "avgHeartRate", stats.getAvgHeartRate(),
                    "finalCalorie", stats.getFinalCalorie()
            ));
        }

        String response = objectMapper.writeValueAsString(Map.of(
                "type", "finalStats",
                "users", finalStatsList
        ));

        // 모든 사용자에게 전송
        if (channels.containsKey(channelId)) {
            for (WebSocketSession s : channels.get(channelId).values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(response));
                }
            }
        }

        System.out.println("Final stats sent to all clients in channel " + channelId + ": " + response);
    }

    private void broadcastMessage(String channelId, WebSocketSession session, String message) throws Exception {
        if (channels.containsKey(channelId)) {
            for (WebSocketSession s : channels.get(channelId).values()) {
                if (s.isOpen() && !s.getId().equals(session.getId())) {
                    s.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String channelId = getChannelId(session);
        if (channels.containsKey(channelId)) {
            channels.get(channelId).remove(session.getId());
            userStats.get(channelId).remove(session.getId());
            System.out.println("Session " + session.getId() + " left channel: " + channelId);
        }
    }

    private String getChannelId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.split("/channel/")[1];
    }

    public List<PunchGameEndResponseDto> endPunchGame(String channelId) {
        if (!userStats.containsKey(channelId)) {
            throw ServiceException.of(ErrorCode.CHANNEL_NOT_FOUND);
        }

        List<PunchGameEndResponseDto> responseDtos = new ArrayList<>();

        for (Map.Entry<String, UserStats> entry : userStats.get(channelId).entrySet()) {
            UserStats stats = entry.getValue();

            // UserStats 데이터를 PunchGameEndResponseDto로 변환
            PunchGameEndResponseDto dto = new PunchGameEndResponseDto(
                    stats.getNickname(),
                    stats.getFinalPunch(),
                    stats.getAvgHeartRate(),
                    stats.getFinalCalorie()
            );

            // 변환된 DTO를 리스트에 추가
            responseDtos.add(dto);
        }

        // 최종 리스트 반환
        return responseDtos;
    }

    private static class UserStats {
        private int finalPunch = 0;
        private double avgHeartRate = 0;
        private double finalCalorie = 0;
        private int heartRateCount = 0;
        private String nickname;

        void updateStats(int punch, double heartRate, double calories, String nickname) {
            this.finalPunch = punch;
            this.avgHeartRate = (this.avgHeartRate * heartRateCount + heartRate) / (++heartRateCount);
            this.finalCalorie += calories;
            this.nickname = nickname;
        }

        public int getFinalPunch() { return finalPunch; }
        public double getAvgHeartRate() { return avgHeartRate; }
        public double getFinalCalorie() { return finalCalorie; }
        public int getHeartRateCount() { return heartRateCount; }
        public String getNickname() { return nickname; }

        @Override
        public String toString() {
            return "UserStats{" +
                    "nickname='" + nickname + '\'' +
                    ", finalPunch=" + finalPunch +
                    ", avgHeartRate=" + avgHeartRate +
                    ", finalCalorie=" + finalCalorie +
                    '}';
        }
    }
}
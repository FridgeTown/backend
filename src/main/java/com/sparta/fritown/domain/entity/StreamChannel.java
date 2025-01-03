package com.sparta.fritown.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class StreamChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title; // 채팅방 이름
    private String thumbnail;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private Boolean isStreaming; // 스트리밍 상태


    @OneToOne
    @JoinColumn(name = "match_id") // Matches와 연결 (FK)
    private Matches match;

    @Builder
    public StreamChannel(String title, String thumbnail, User owner, Matches match) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.owner = owner;
        this.match = match;
        this.isStreaming = false;
    }

    public void updateIsStreaming(Boolean isStreaming) {
        this.isStreaming = isStreaming;
    }

    public void validateChannelOwner(Long userId, StreamChannel channel) {
        if(!channel.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not owner of this channel");
        }
    }
}

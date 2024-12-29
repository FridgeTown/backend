package com.sparta.fritown.domain.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
public class KlatCreateChannelRequestDto {
    String name;
    String ownerId;
    String type;
    List<String> members;
    String category;

    public KlatCreateChannelRequestDto(String name, String ownerId, String type, List<String> members, String category) {
        this.name = name;
        this.ownerId = ownerId;
        this.type = type;
        this.members = members;
        this.category = category;
    }
}

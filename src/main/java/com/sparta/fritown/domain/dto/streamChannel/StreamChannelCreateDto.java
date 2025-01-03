package com.sparta.fritown.domain.dto.streamChannel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamChannelCreateDto {
    Long matchId;
    String title;
    String place;
    String thumbnail;
}

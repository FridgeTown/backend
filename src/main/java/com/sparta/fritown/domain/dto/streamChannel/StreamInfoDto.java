package com.sparta.fritown.domain.dto.streamChannel;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamInfoDto {
     Long id;
     String title;
     String place;
     String challengedToUserNickname;
     String challengedByUserNickname;
}

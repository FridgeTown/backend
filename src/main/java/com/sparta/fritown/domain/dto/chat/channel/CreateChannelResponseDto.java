package com.sparta.fritown.domain.dto.chat.channel;

import com.sparta.fritown.domain.dto.chat.channel.component.ChannelDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChannelResponseDto {
    private ChannelDto channel;
}

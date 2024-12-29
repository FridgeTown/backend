package com.sparta.fritown.domain.dto.chat.channel;

import com.sparta.fritown.domain.dto.chat.channel.component.ChannelDto;

import java.util.List;

public class GetUserChannelsResponseDto {
    private List<ChannelDto> channels;
    private boolean hasNext;
}

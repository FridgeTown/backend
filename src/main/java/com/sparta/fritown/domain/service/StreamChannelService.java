package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.streamChannel.StreamInfoDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.dto.streamChannel.StreamChannelCreateDto;
import com.sparta.fritown.domain.entity.StreamChannel;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.StreamChannelRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.grpc.NodeGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamChannelService {
    private final StreamChannelRepository streamChannelRepository;
    private final UserRepository userRepository;
    private final MatchesRepository matchesRepository;

    private final NodeGrpcClient nodeGrpcClient;

    @Transactional
    public StreamChannel create(StreamChannelCreateDto chatRoomDto, Long userId) {
        Matches match = matchesRepository.findById(chatRoomDto.getMatchId()).orElseThrow(() -> new IllegalArgumentException("Match not found"));
        User owner = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        match.validateMatchedUserId(owner.getId());
        StreamChannel channel = streamChannelRepository.save(StreamChannel.builder()
                .title(chatRoomDto.getTitle())
                .thumbnail(chatRoomDto.getThumbnail())
                .owner(owner)
                .match(match)
                .build());

        if(nodeGrpcClient.createStream(channel.getId()).equals(false)){
            handleGrpcFailure(channel.getId());
            throw new IllegalArgumentException("Failed to create stream via gRPC. Transaction rolled back.\"");
        }

        return channel;
    }

    public StreamInfoDto getStreamChannelInfo(Long chatRoomId) {
        StreamChannel streamChannel = streamChannelRepository.findById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("StreamChannel not found"));
        return createStreamInfoDto(streamChannel);
    }

    public StreamChannel getStreamChannelById(Long chatRoomId) {
        return streamChannelRepository.findById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("StreamChannel not found"));
    }

    public List<StreamInfoDto> getAll() {
        return streamChannelRepository.findAll().stream()
                .filter(this::isChannelStreaming)
                .map(this::createStreamInfoDto)
                .toList();
    }

    public List<StreamInfoDto> getMyStreamChannels(Long userId) {
        return streamChannelRepository.findAll().stream()
                .filter((streamChannel) ->isMyChannel(streamChannel, userId))
                .map(this::createStreamInfoDto)
                .toList();
    }

    private boolean isMyChannel(StreamChannel streamChannel, Long userId) {
        return streamChannel.getOwner().getId().equals(userId);
    }

    private boolean isChannelStreaming(StreamChannel streamChannel) {
        return streamChannel.getIsStreaming();
    }

    private StreamInfoDto createStreamInfoDto(StreamChannel streamChannel) {
        return StreamInfoDto.builder()
                .id(streamChannel.getId())
                .title(streamChannel.getTitle())
                .place(streamChannel.getMatch().getPlace())
                .challengedByUserNickname(streamChannel.getMatch().getChallengedBy().getNickname())
                .challengedToUserNickname(streamChannel.getMatch().getChallengedTo().getNickname())
                .build();
    }

    @Transactional
    public void deleteById(Long chatRoomId, Long userId) {
        StreamChannel channel = streamChannelRepository.findById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("StreamChannel not found"));
        channel.validateChannelOwner(userId, channel);
        streamChannelRepository.delete(channel);
        if(nodeGrpcClient.deleteStream(chatRoomId).equals(false)) {
            streamChannelRepository.save(channel);
            throw new IllegalArgumentException("Failed to delete stream via gRPC. Transaction rolled back.\"");
        }
    }

    @Transactional
    public void startStreaming(Long id, Long userId) {
        StreamChannel channel = getStreamChannelById(id);
        channel.validateChannelOwner(userId, channel);
        channel.updateIsStreaming(true);
        streamChannelRepository.save(channel);
        if(nodeGrpcClient.startStreaming(id).equals(false)) {
            channel.updateIsStreaming(false);
            streamChannelRepository.save(channel);
            throw new IllegalArgumentException("Failed to stop streaming via gRPC\"");
        }
    }

    @Transactional
    public void stopStreaming(Long id, Long userId) {
        StreamChannel channel = getStreamChannelById(id);
        channel.validateChannelOwner(userId, channel);
        channel.updateIsStreaming(false);
        streamChannelRepository.save(channel);
        if(nodeGrpcClient.stopStreaming(id).equals(false)) {
            channel.updateIsStreaming(true);
            streamChannelRepository.save(channel);
            throw new IllegalArgumentException("Failed to stop streaming via gRPC\"");
        }
    }

    private void handleGrpcFailure(Long channelId) {
        streamChannelRepository.deleteById(channelId);
    }
}

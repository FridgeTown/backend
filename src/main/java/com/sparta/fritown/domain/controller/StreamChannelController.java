package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.streamChannel.StreamChannelCreateDto;
import com.sparta.fritown.domain.dto.streamChannel.StreamInfoDto;
import com.sparta.fritown.domain.entity.StreamChannel;
import com.sparta.fritown.domain.service.StreamChannelService;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stream")
@RequiredArgsConstructor
public class StreamChannelController {

    private final StreamChannelService streamChannelService;

    @GetMapping("/my")
    public ResponseEntity<List<StreamInfoDto>> getMyStreamChannels(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<StreamInfoDto> streamChannels = streamChannelService.getMyStreamChannels(userDetails.getId());
        return ResponseEntity.ok(streamChannels);
    }

    @GetMapping
    public ResponseEntity<List<StreamInfoDto>> getAllStreamChannels() {
        List<StreamInfoDto> streamChannels = streamChannelService.getAll();

        return ResponseEntity.ok(streamChannels);
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<StreamInfoDto> getStreamChannelById(@PathVariable Long channelId) {
        StreamInfoDto streamChannel = streamChannelService.getStreamChannelInfo(channelId);

        return ResponseEntity.ok(streamChannel);
    }

    @PostMapping
    public ResponseEntity<Long> createStreamChannel(@RequestBody StreamChannelCreateDto streamChannelCreateDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StreamChannel createdStreamChannel = streamChannelService.create(streamChannelCreateDto, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdStreamChannel.getId());
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Long> deleteStreamChannel(@PathVariable Long channelId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        streamChannelService.deleteById(channelId, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/start")
    public ResponseEntity<Void> startStreaming(@PathVariable Long channelId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        streamChannelService.startStreaming(channelId, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/stop")
    public ResponseEntity<Void> stopStreaming(@PathVariable Long channelId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        streamChannelService.stopStreaming(channelId, userDetails.getId());

        return ResponseEntity.ok().build();
    }
}


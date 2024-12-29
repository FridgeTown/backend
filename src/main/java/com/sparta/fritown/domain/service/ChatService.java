package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.chat.channel.CreateChannelRequestDto;
import com.sparta.fritown.domain.dto.chat.channel.CreateChannelResponseDto;
import com.sparta.fritown.domain.dto.chat.channel.GetUserChannelsResponseDto;
import com.sparta.fritown.domain.dto.chat.user.CreateUserRequestDto;
import com.sparta.fritown.domain.dto.chat.user.CreateUserResponseDto;
import com.sparta.fritown.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
public class ChatService {

    @Value("${talkplus.app-id}")
    private String appId;

    @Value("${talkplus.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 사용자 생성


    // 채널 생성
    public void acceptAndCreateChannel(List<User> userList, String type, String chatRoomName)
    {
        // 채팅방에 참여하고 있는 유저의 닉네임 리스트
        List<String> userNicknameList = new ArrayList<>();
        for (User user: userList)
        {
            userNicknameList.add(user.getNickname());
        }

        CreateChannelRequestDto request = new CreateChannelRequestDto();
        request.setMembers(userNicknameList);
        request.setType(type);
        request.setName(chatRoomName);

        createChannel(request);
    }




    private CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        // API URL 설정
        String userCreateApiUrl = "https://api.talkplus.io/v1.4/api/users";

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        // HttpEntity 생성(요청 본문과 헤더 포함)
        HttpEntity<CreateUserRequestDto> request = new HttpEntity<>(createUserRequestDto, headers);

        // API 호출
        ResponseEntity<CreateUserResponseDto> response = restTemplate.exchange(
                userCreateApiUrl,
                HttpMethod.POST,
                request,
                CreateUserResponseDto.class
        );

        return response.getBody();
    }

    private CreateChannelResponseDto createChannel(CreateChannelRequestDto createChannelRequestDto) {
        // API URL 설정
        String channelCreateApiUrl = "https://api.talkplus.io/v1.4/api/channels/create";

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        // HTTP Entity 생성
        HttpEntity<CreateChannelRequestDto> request = new HttpEntity<>(createChannelRequestDto, headers);

        // API 호출
        ResponseEntity<CreateChannelResponseDto> response = restTemplate.exchange(
                channelCreateApiUrl,
                HttpMethod.POST,
                request,
                CreateChannelResponseDto.class
        );

        return response.getBody();

    }

    private GetUserChannelsResponseDto getUserChannels(String userId, String category,String subcategory ,String lastChannelId)
    {
        // API URL 설정
        String url = "https://api.talkplus.io/v1.4/api/users/" + userId + "/channels";
        // Query Parameters 설정
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (category != null) uriBuilder.queryParam("category", category);
        if (subcategory != null) uriBuilder.queryParam("subcategory", subcategory);
        if (lastChannelId != null) uriBuilder.queryParam("lastChannelId", lastChannelId);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<GetUserChannelsResponseDto> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                GetUserChannelsResponseDto.class
        );

        return response.getBody();
    }

}

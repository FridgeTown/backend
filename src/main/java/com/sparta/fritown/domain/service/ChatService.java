package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.chat.channel.CreateChannelRequestDto;
import com.sparta.fritown.domain.dto.chat.channel.CreateChannelResponseDto;
import com.sparta.fritown.domain.dto.chat.channel.GetUserChannelsRequestDto;
import com.sparta.fritown.domain.dto.chat.channel.GetUserChannelsResponseDto;
import com.sparta.fritown.domain.dto.chat.user.CreateUserRequestDto;
import com.sparta.fritown.domain.dto.chat.user.CreateUserResponseDto;
import com.sparta.fritown.domain.dto.chat.user.ChatLoginRequestDto;
import com.sparta.fritown.domain.dto.chat.user.ChatLoginResponseDto;
import com.sparta.fritown.domain.dto.user.LoginRequestDto;
import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
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
    // AuthController signup 에서 사용
    // userId 와 loginToken 을 넘겨주어야 한다.
    public Map<String,String> acceptAndCreateUser(RegisterRequestDto registerRequestDto)
    {
        String userId = registerRequestDto.getEmail(); // 추후에 변경 예정
        String password = "abcdefg1234"; // 추후에 변경 예정

        CreateUserRequestDto request = new CreateUserRequestDto();
        request.setUserId(userId);
        request.setPassword(password);

        CreateUserResponseDto response = createUser(request);

        Map<String, String> result = new HashMap<>();
        result.put("userId",response.getChatUserDto().getId());
        result.put("loginToken", response.getLoginToken());

        return result;

    }

    // 사용자 로그인
    // AuthController login 에서 사용
    // userId 와 loginToken 을 넘겨주어야 한다.
    public Map<String,String> acceptAndLoginUser(LoginRequestDto loginRequestDto)
    {
        String userId = loginRequestDto.getIdToken(); // 추후에 변경 예정
        String password = loginRequestDto.getProvider(); // 추후에 변경 예정

        ChatLoginRequestDto request = new ChatLoginRequestDto();
        request.setUserId(userId);
        request.setPassword(password);

        ChatLoginResponseDto response = loginUser(request);

        Map<String, String> result = new HashMap<>();
        result.put("userId",response.getChatUserDto().getId());
        result.put("loginToken", response.getLoginToken());

        return result;

    }

    // 채널 생성
    // MatchService 에서 사용
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

    // 채널 삭제
    // ChatController 에서 사용


    // 사용자 채널 조회
    // ChatController 에서 사용
    // GetUserChannelsResponseDto 를 넘겨주어야 할듯
    public GetUserChannelsResponseDto acceptAndGetUserChannels(String userId)
    {
        GetUserChannelsRequestDto request = new GetUserChannelsRequestDto(userId);
        request.setUserId(userId);

        return getUserChannels(request);
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

    private ChatLoginResponseDto loginUser(ChatLoginRequestDto chatLoginRequestDto)
    {
        String url = "https://api.talkplus.io/v1.4/api/users/login";
        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("app-id", appId); // App ID 설정
        headers.set("api-key", apiKey); // API Key 설정

        // HTTP 요청 생성
        HttpEntity<ChatLoginRequestDto> requestEntity = new HttpEntity<>(chatLoginRequestDto, headers);

        // API 호출
        ResponseEntity<ChatLoginResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ChatLoginResponseDto.class
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

    private GetUserChannelsResponseDto getUserChannels(GetUserChannelsRequestDto getUserChannelsRequestDto)
    {
        String userId = getUserChannelsRequestDto.getUserId();
        String category = getUserChannelsRequestDto.getCategory();
        String subcategory = getUserChannelsRequestDto.getSubcategory();
        String lastChannelId = getUserChannelsRequestDto.getLastChannelId();

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

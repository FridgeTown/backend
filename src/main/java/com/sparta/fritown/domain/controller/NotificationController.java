package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.service.NotificationService;
import com.sparta.fritown.global.docs.NotificationControllerDocs;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.subscribe(userDetails.getId());
    }


}

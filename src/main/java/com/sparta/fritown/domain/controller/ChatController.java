package com.sparta.fritown.domain.controller;

import com.sparta.fritown.global.docs.ChatControllerDocs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController implements ChatControllerDocs {
}

package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.service.TestService;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.global.docs.TestControllerDocs;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController implements TestControllerDocs {

}

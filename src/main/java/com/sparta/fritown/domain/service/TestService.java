package com.sparta.fritown.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {


    public void healthCheck() {
      log.info("hihihi");
    }
}

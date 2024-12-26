package com.sparta.fritown.domain.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {


    public void healthCheck() {
      log.info("hihihi");
    }
}

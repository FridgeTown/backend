package com.sparta.fritown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.sparta.fritown.domain.entity")
public class FriTownApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriTownApplication.class, args);
    }

}

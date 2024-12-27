package com.sparta.fritown;

import com.sparta.fritown.global.security.util.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EntityScan(basePackages = "com.sparta.fritown.domain.entity")
public class FriTownApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriTownApplication.class, args);
    }

}

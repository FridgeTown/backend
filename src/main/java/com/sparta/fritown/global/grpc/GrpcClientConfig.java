package com.sparta.fritown.global.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 50051) // gRPC 서버 주소와 포트
                .usePlaintext() // SSL 비활성화 (개발 환경에서만)
                .build();
    }
}

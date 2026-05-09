package com.example.postservice.config;

import com.example.aiserviceproto.AiServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${ai-service.grpc.host:localhost}")
    private String aiServiceHost;

    @Value("${ai-service.grpc.port:9090}")
    private int aiServicePort;

    @Bean
    public ManagedChannel aiServiceGrpcChannel() {
        return ManagedChannelBuilder
                .forAddress(aiServiceHost, aiServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public AiServiceGrpc.AiServiceBlockingStub aiServiceBlockingStub(ManagedChannel aiServiceGrpcChannel) {
        return AiServiceGrpc.newBlockingStub(aiServiceGrpcChannel);
    }
}

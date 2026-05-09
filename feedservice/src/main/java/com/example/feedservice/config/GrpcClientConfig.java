package com.example.feedservice.config;

import com.example.userserviceproto.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${user-service.grpc.host:localhost}") private String userServiceHost;
    @Value("${user-service.grpc.port:9091}") private int userServicePort;

    @Bean
    public ManagedChannel userServiceGrpcChannel() {
        return ManagedChannelBuilder.forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(ManagedChannel userServiceGrpcChannel) {
        return UserServiceGrpc.newBlockingStub(userServiceGrpcChannel);
    }
}

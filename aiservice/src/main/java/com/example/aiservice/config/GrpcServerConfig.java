package com.example.aiservice.config;

import com.example.aiservice.grpc.AiServiceGrpcImpl;
import com.example.aiservice.service.ChatService;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GrpcServerConfig implements SmartLifecycle {

    private final ChatService chatService;

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private Server server;
    private volatile boolean running = false;

    @Override
    public void start() {
        try {
            server = NettyServerBuilder.forPort(grpcPort)
                    .addService(new AiServiceGrpcImpl(chatService))
                    .build()
                    .start();
            running = true;
            log.info("gRPC server started on port {}", grpcPort);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start gRPC server", e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
            running = false;
            log.info("gRPC server stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}

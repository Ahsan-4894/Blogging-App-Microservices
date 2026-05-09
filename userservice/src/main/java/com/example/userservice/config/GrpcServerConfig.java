package com.example.userservice.config;

import com.example.userservice.grpc.UserServiceGrpcImpl;
import com.example.userservice.service.UserService;
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

    private final UserService userService;

    @Value("${grpc.server.port:9091}")
    private int grpcPort;

    private Server server;
    private volatile boolean running = false;

    @Override
    public void start() {
        try {
            server = NettyServerBuilder.forPort(grpcPort)
                    .addService(new UserServiceGrpcImpl(userService))
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

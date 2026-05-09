package com.example.aiservice.grpc;

import com.example.aiservice.service.ChatService;
import com.example.aiserviceproto.AiServiceGrpc;
import com.example.aiserviceproto.GenerateTagsRequest;
import com.example.aiserviceproto.GenerateTagsResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AiServiceGrpcImpl extends AiServiceGrpc.AiServiceImplBase {

    private final ChatService chatService;

    @Override
    public void generateTags(GenerateTagsRequest request, StreamObserver<GenerateTagsResponse> responseObserver) {
        try {
            String tags = chatService.generateTags(request.getContent());
            GenerateTagsResponse response = GenerateTagsResponse.newBuilder()
                    .setTags(tags)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to generate tags", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}

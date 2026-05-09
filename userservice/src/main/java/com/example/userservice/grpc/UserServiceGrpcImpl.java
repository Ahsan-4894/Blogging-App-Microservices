package com.example.userservice.grpc;

import com.example.userservice.service.UserService;
import com.example.userserviceproto.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void getFollowerEmails(GetFollowerEmailsRequest request,
                                  StreamObserver<GetFollowerEmailsResponse> responseObserver) {
        try {
            log.info("gRPC getFollowerEmails called for userId={}", request.getUserId());
            GetFollowerEmailsResponse response = GetFollowerEmailsResponse.newBuilder()
                    .addAllEmails(userService.getFollowerEmails(request.getUserId()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to get follower emails for userId={}: {}", request.getUserId(), e.getMessage());
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getFollowerIds(GetFollowerIdsRequest request,
                               StreamObserver<GetFollowerIdsRespone> responseObserver){
        try{
            log.info("gRPC getFollowerEmails called for userId={}", request.getUserId());
            GetFollowerIdsRespone response = GetFollowerIdsRespone.newBuilder()
                    .addAllIds(userService.getFollowerIds(request.getUserId()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch(Exception e){
            log.error("Failed to get follower ids for userId={}: {}", request.getUserId(), e.getMessage());
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

}

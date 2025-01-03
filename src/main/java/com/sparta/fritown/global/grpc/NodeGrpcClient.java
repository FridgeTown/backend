package com.sparta.fritown.global.grpc;

import com.example.grpc.NodeServiceGrpc;
import com.example.grpc.NodeServiceOuterClass.Request;
import io.grpc.ManagedChannel;
import org.springframework.stereotype.Component;

@Component
public class NodeGrpcClient {

    private final NodeServiceGrpc.NodeServiceBlockingStub blockingStub;

    public NodeGrpcClient(ManagedChannel channel) {
        this.blockingStub = NodeServiceGrpc.newBlockingStub(channel);
    }

    public Boolean createStream(Long channelId) {
        Request request = Request.newBuilder()
                .setChannelId(channelId)
                .build();

        return blockingStub.createStream(request).getSuccess();
    }

    public Boolean deleteStream(Long channelId) {
        Request request = Request.newBuilder()
                .setChannelId(channelId)
                .build();

        return blockingStub.deleteStream(request).getSuccess();
    }

    public Boolean startStreaming(Long channelId) {
        Request request = Request.newBuilder()
                .setChannelId(channelId)
                .build();

        return blockingStub.startStreaming(request).getSuccess();
    }

    public Boolean stopStreaming(Long channelId) {
        Request request = Request.newBuilder()
                .setChannelId(channelId)
                .build();

        return blockingStub.stopStreaming(request).getSuccess();
    }
}
package org.iotp.server.gen.cluster;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.2)",
    comments = "Source: cluster.proto")
public class ClusterRpcServiceGrpc {

  private ClusterRpcServiceGrpc() {}

  public static final String SERVICE_NAME = "cluster.ClusterRpcService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage,
      org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage> METHOD_HANDLE_PLUGIN_MSGS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING,
          generateFullMethodName(
              "cluster.ClusterRpcService", "handlePluginMsgs"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ClusterRpcServiceStub newStub(io.grpc.Channel channel) {
    return new ClusterRpcServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ClusterRpcServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ClusterRpcServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static ClusterRpcServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ClusterRpcServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ClusterRpcServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage> handlePluginMsgs(
        io.grpc.stub.StreamObserver<org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_HANDLE_PLUGIN_MSGS, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_HANDLE_PLUGIN_MSGS,
            asyncBidiStreamingCall(
              new MethodHandlers<
                org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage,
                org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage>(
                  this, METHODID_HANDLE_PLUGIN_MSGS)))
          .build();
    }
  }

  /**
   */
  public static final class ClusterRpcServiceStub extends io.grpc.stub.AbstractStub<ClusterRpcServiceStub> {
    private ClusterRpcServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClusterRpcServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClusterRpcServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClusterRpcServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage> handlePluginMsgs(
        io.grpc.stub.StreamObserver<org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(METHOD_HANDLE_PLUGIN_MSGS, getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class ClusterRpcServiceBlockingStub extends io.grpc.stub.AbstractStub<ClusterRpcServiceBlockingStub> {
    private ClusterRpcServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClusterRpcServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClusterRpcServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClusterRpcServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class ClusterRpcServiceFutureStub extends io.grpc.stub.AbstractStub<ClusterRpcServiceFutureStub> {
    private ClusterRpcServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClusterRpcServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClusterRpcServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClusterRpcServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_HANDLE_PLUGIN_MSGS = 0;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ClusterRpcServiceImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(ClusterRpcServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_PLUGIN_MSGS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.handlePluginMsgs(
              (io.grpc.stub.StreamObserver<org.iotp.server.gen.cluster.ClusterAPIProtos.ToRpcServerMessage>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_HANDLE_PLUGIN_MSGS);
  }

}

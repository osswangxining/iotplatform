package org.iotp.server.actors.rpc;

import java.util.UUID;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.gen.cluster.ClusterAPIProtos;
import org.iotp.server.gen.cluster.ClusterRpcServiceGrpc;
import org.iotp.server.service.cluster.rpc.GrpcSession;
import org.iotp.server.service.cluster.rpc.GrpcSessionListener;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 */
public class RpcSessionActor extends ContextAwareActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  private final UUID sessionId;
  private GrpcSession session;
  private GrpcSessionListener listener;

  public RpcSessionActor(ActorSystemContext systemContext, UUID sessionId) {
    super(systemContext);
    this.sessionId = sessionId;
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof RpcSessionTellMsg) {
      tell((RpcSessionTellMsg) msg);
    } else if (msg instanceof RpcSessionCreateRequestMsg) {
      initSession((RpcSessionCreateRequestMsg) msg);
    }
  }

  private void tell(RpcSessionTellMsg msg) {
    session.sendMsg(msg.getMsg());
  }

  @Override
  public void postStop() {
    log.info("Closing session -> {}", session.getRemoteServer());
    session.close();
  }

  private void initSession(RpcSessionCreateRequestMsg msg) {
    log.info("[{}] Initializing session", context().self());
    ServerAddress remoteServer = msg.getRemoteAddress();
    listener = new BasicRpcSessionListener(systemContext, context().parent(), context().self());
    if (msg.getRemoteAddress() == null) {
      // Server session
      session = new GrpcSession(listener);
      session.setOutputStream(msg.getResponseObserver());
      session.initInputStream();
      session.initOutputStream();
      systemContext.getRpcService().onSessionCreated(msg.getMsgUid(), session.getInputStream());
    } else {
      // Client session
      Channel channel = ManagedChannelBuilder.forAddress(remoteServer.getHost(), remoteServer.getPort())
          .usePlaintext(true).build();
      session = new GrpcSession(remoteServer, listener);
      session.initInputStream();

      ClusterRpcServiceGrpc.ClusterRpcServiceStub stub = ClusterRpcServiceGrpc.newStub(channel);
      StreamObserver<ClusterAPIProtos.ToRpcServerMessage> outputStream = stub
          .handlePluginMsgs(session.getInputStream());

      session.setOutputStream(outputStream);
      session.initOutputStream();
      outputStream.onNext(toConnectMsg());
    }
  }

  public static class ActorCreator extends ContextBasedCreator<RpcSessionActor> {
    private static final long serialVersionUID = 1L;

    private final UUID sessionId;

    public ActorCreator(ActorSystemContext context, UUID sessionId) {
      super(context);
      this.sessionId = sessionId;
    }

    @Override
    public RpcSessionActor create() throws Exception {
      return new RpcSessionActor(context, sessionId);
    }
  }

  private ClusterAPIProtos.ToRpcServerMessage toConnectMsg() {
    ServerAddress instance = systemContext.getDiscoveryService().getCurrentServer().getServerAddress();
    return ClusterAPIProtos.ToRpcServerMessage.newBuilder()
        .setConnectMsg(ClusterAPIProtos.ConnectRpcMessage.newBuilder().setServerAddress(
            ClusterAPIProtos.ServerAddress.newBuilder().setHost(instance.getHost()).setPort(instance.getPort()).build())
            .build())
        .build();

  }
}

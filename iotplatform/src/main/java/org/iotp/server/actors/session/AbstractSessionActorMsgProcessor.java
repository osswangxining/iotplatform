package org.iotp.server.actors.session;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.asset.BasicToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.device.BasicToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.AdaptorToSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionContext;
import org.iotp.analytics.ruleengine.common.msg.session.SessionCtrlMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ToAssetActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ctrl.SessionCloseMsg;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.shared.AbstractContextAwareMsgProcessor;
import org.iotp.server.actors.shared.SessionTimeoutMsg;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;

abstract class AbstractSessionActorMsgProcessor extends AbstractContextAwareMsgProcessor {

  protected final SessionId sessionId;
  protected SessionContext sessionCtx;
  protected ToDeviceActorMsg toDeviceActorMsgPrototype;
  protected ToAssetActorMsg toAssetActorMsgPrototype;

  protected AbstractSessionActorMsgProcessor(ActorSystemContext ctx, LoggingAdapter logger, SessionId sessionId) {
    super(ctx, logger);
    this.sessionId = sessionId;
  }

  protected abstract void processToDeviceActorMsg(ActorContext ctx, ToDeviceActorSessionMsg msg);

  protected abstract void processToAssetActorMsg(ActorContext ctx, ToAssetActorSessionMsg msg);

  protected abstract void processTimeoutMsg(ActorContext context, SessionTimeoutMsg msg);

  protected abstract void processToDeviceMsg(ActorContext context, ToDeviceMsg msg);

  protected abstract void processToAssetMsg(ActorContext context, ToDeviceMsg msg);

  public abstract void processClusterEvent(ActorContext context, ClusterEventMsg msg);

  protected void processSessionCtrlMsg(ActorContext ctx, SessionCtrlMsg msg) {
    if (msg instanceof SessionCloseMsg) {
      cleanupSession(ctx);
      terminateSession(ctx, sessionId);
    }
  }

  protected void cleanupSession(ActorContext ctx) {
  }

  protected void updateSessionCtx(ToDeviceActorSessionMsg msg, SessionType type) {
    sessionCtx = msg.getSessionMsg().getSessionContext();
    toDeviceActorMsgPrototype = new BasicToDeviceActorMsg(msg, type);
  }

  protected void updateSessionCtx(ToAssetActorSessionMsg msg, SessionType type) {
    sessionCtx = msg.getSessionMsg().getSessionContext();
    toAssetActorMsgPrototype = new BasicToAssetActorMsg(msg, type);
  }

  protected ToDeviceActorMsg toDeviceMsg(ToDeviceActorSessionMsg msg) {
    AdaptorToSessionActorMsg adaptorMsg = msg.getSessionMsg();
    return new BasicToDeviceActorMsg(toDeviceActorMsgPrototype, adaptorMsg.getMsg());
  }

  protected ToAssetActorMsg toAssetMsg(ToAssetActorSessionMsg msg) {
    AdaptorToSessionActorMsg adaptorMsg = msg.getSessionMsg();
    return new BasicToAssetActorMsg(toAssetActorMsgPrototype, adaptorMsg.getMsg());
  }

  protected Optional<ToDeviceActorMsg> toDeviceMsg(FromDeviceMsg msg) {
    if (toDeviceActorMsgPrototype != null) {
      return Optional.of(new BasicToDeviceActorMsg(toDeviceActorMsgPrototype, msg));
    } else {
      return Optional.empty();
    }
  }

  protected Optional<ToAssetActorMsg> toAssetMsg(FromDeviceMsg msg) {
    if (toAssetActorMsgPrototype != null) {
      return Optional.of(new BasicToAssetActorMsg(toAssetActorMsgPrototype, msg));
    } else {
      return Optional.empty();
    }
  }

  protected Optional<ServerAddress> forwardToAppActor(ActorContext ctx, ToDeviceActorMsg toForward) {
    Optional<ServerAddress> address = systemContext.getRoutingService().resolveById(toForward.getDeviceId());
    forwardToAppActor(ctx, toForward, address);
    return address;
  }

  protected Optional<ServerAddress> forwardToAppActor(ActorContext ctx, ToAssetActorMsg toForward) {
    Optional<ServerAddress> address = systemContext.getRoutingService().resolveById(toForward.getAssetId());
    forwardToAppActor(ctx, toForward, address);
    return address;
  }

  protected Optional<ServerAddress> forwardToAppActorIfAdressChanged(ActorContext ctx, ToDeviceActorMsg toForward,
      Optional<ServerAddress> oldAddress) {
    Optional<ServerAddress> newAddress = systemContext.getRoutingService().resolveById(toForward.getDeviceId());
    if (!newAddress.equals(oldAddress)) {
      if (newAddress.isPresent()) {
        systemContext.getRpcService().tell(newAddress.get(),
            toForward.toOtherAddress(systemContext.getRoutingService().getCurrentServer()));
      } else {
        getAppActor().tell(toForward, ctx.self());
      }
    }
    return newAddress;
  }

  protected void forwardToAppActor(ActorContext ctx, ToDeviceActorMsg toForward, Optional<ServerAddress> address) {
    if (address.isPresent()) {
      systemContext.getRpcService().tell(address.get(),
          toForward.toOtherAddress(systemContext.getRoutingService().getCurrentServer()));
    } else {
      getAppActor().tell(toForward, ctx.self());
    }
  }

  protected void forwardToAppActor(ActorContext ctx, ToAssetActorMsg toForward, Optional<ServerAddress> address) {
    if (address.isPresent()) {
      systemContext.getRpcService().tell(address.get(),
          toForward.toOtherAddress(systemContext.getRoutingService().getCurrentServer()));
    } else {
      getAppActor().tell(toForward, ctx.self());
    }
  }

  public static void terminateSession(ActorContext ctx, SessionId sessionId) {
    ctx.parent().tell(new SessionTerminationMsg(sessionId), ActorRef.noSender());
    ctx.stop(ctx.self());
  }

  public DeviceId getDeviceId() {
    return toDeviceActorMsgPrototype.getDeviceId();
  }

  public AssetId getAssetId() {
    return toAssetActorMsgPrototype.getAssetId();
  }
}

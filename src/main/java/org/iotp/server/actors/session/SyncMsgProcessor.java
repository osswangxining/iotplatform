package org.iotp.server.actors.session;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.BasicSessionActorToAdaptorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionContext;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ToAssetActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ctrl.SessionCloseMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ex.SessionException;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.shared.SessionTimeoutMsg;

import akka.actor.ActorContext;
import akka.event.LoggingAdapter;

class SyncMsgProcessor extends AbstractSessionActorMsgProcessor {
  private ToDeviceActorMsg pendingMsg;
  private ToAssetActorMsg pendingMsg4Asset;
  private Optional<ServerAddress> currentTargetServer;
  private boolean pendingResponse;

  public SyncMsgProcessor(ActorSystemContext ctx, LoggingAdapter logger, SessionId sessionId) {
    super(ctx, logger, sessionId);
  }

  @Override
  protected void processToDeviceActorMsg(ActorContext ctx, ToDeviceActorSessionMsg msg) {
    updateSessionCtx(msg, SessionType.SYNC);
    pendingMsg = toDeviceMsg(msg);
    pendingResponse = true;
    currentTargetServer = forwardToAppActor(ctx, pendingMsg);
    scheduleMsgWithDelay(ctx, new SessionTimeoutMsg(sessionId),
        getTimeout(systemContext, msg.getSessionMsg().getSessionContext()), ctx.parent());
  }

  @Override
  protected void processToAssetActorMsg(ActorContext ctx, ToAssetActorSessionMsg msg) {
    updateSessionCtx(msg, SessionType.SYNC);
    pendingMsg4Asset = toAssetMsg(msg);
    pendingResponse = true;
    currentTargetServer = forwardToAppActor(ctx, pendingMsg4Asset);
    scheduleMsgWithDelay(ctx, new SessionTimeoutMsg(sessionId),
        getTimeout(systemContext, msg.getSessionMsg().getSessionContext()), ctx.parent());
  }

  public void processTimeoutMsg(ActorContext context, SessionTimeoutMsg msg) {
    if (pendingResponse) {
      try {
        sessionCtx.onMsg(SessionCloseMsg.onTimeout(sessionId));
      } catch (SessionException e) {
        logger.warning("Failed to push session close msg", e);
      }
      terminateSession(context, this.sessionId);
    }
  }

  public void processToDeviceMsg(ActorContext context, ToDeviceMsg msg) {
    try {
      sessionCtx.onMsg(new BasicSessionActorToAdaptorMsg(this.sessionCtx, msg));
      pendingResponse = false;
    } catch (SessionException e) {
      logger.warning("Failed to push session response msg", e);
    }
    terminateSession(context, this.sessionId);
  }

  public void processToAssetMsg(ActorContext context, ToDeviceMsg msg) {
    try {
      sessionCtx.onMsg(new BasicSessionActorToAdaptorMsg(this.sessionCtx, msg));
      pendingResponse = false;
    } catch (SessionException e) {
      logger.warning("Failed to push session response msg", e);
    }
    terminateSession(context, this.sessionId);
  }

  @Override
  public void processClusterEvent(ActorContext context, ClusterEventMsg msg) {
    if (pendingResponse) {
      if (pendingMsg != null) {
        Optional<ServerAddress> newTargetServer = forwardToAppActorIfAdressChanged(context, pendingMsg,
            currentTargetServer);
        if (logger.isDebugEnabled()) {
          if (!newTargetServer.equals(currentTargetServer)) {
            if (newTargetServer.isPresent()) {
              logger.debug("[{}] Forwarded msg to new server: {}", sessionId, newTargetServer.get());
            } else {
              logger.debug("[{}] Forwarded msg to local server.", sessionId);
            }
          }
        }

        currentTargetServer = newTargetServer;
      } else if (pendingMsg4Asset != null) {
        Optional<ServerAddress> newTargetServer = forwardToAppActorIfAdressChanged(context, pendingMsg,
            currentTargetServer);
        if (logger.isDebugEnabled()) {
          if (!newTargetServer.equals(currentTargetServer)) {
            if (newTargetServer.isPresent()) {
              logger.debug("[{}] Forwarded msg to new server: {}", sessionId, newTargetServer.get());
            } else {
              logger.debug("[{}] Forwarded msg to local server.", sessionId);
            }
          }
        }

        currentTargetServer = newTargetServer;
      }
    }
  }

  private long getTimeout(ActorSystemContext ctx, SessionContext sessionCtx) {
    return sessionCtx.getTimeout() > 0 ? sessionCtx.getTimeout() : ctx.getSyncSessionTimeout();
  }
}

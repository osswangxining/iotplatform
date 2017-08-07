package org.iotp.server.actors.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.asset.ToAssetActorMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.core.AttributesSubscribeMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ResponseMsg;
import org.iotp.analytics.ruleengine.common.msg.core.RpcSubscribeMsg;
import org.iotp.analytics.ruleengine.common.msg.core.SessionCloseMsg;
import org.iotp.analytics.ruleengine.common.msg.core.SessionOpenMsg;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.BasicSessionActorToAdaptorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceRequestMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ToAssetActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ex.SessionException;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.shared.SessionTimeoutMsg;

import akka.actor.ActorContext;
import akka.event.LoggingAdapter;

class ASyncMsgProcessor extends AbstractSessionActorMsgProcessor {

  private boolean firstMsg = true;
  private Map<Integer, ToDeviceActorMsg> pendingMap = new HashMap<>();
  private Map<Integer, ToAssetActorMsg> pendingMap4Asset = new HashMap<>();
  private Optional<ServerAddress> currentTargetServer;
  private boolean subscribedToAttributeUpdates;
  private boolean subscribedToRpcCommands;

  public ASyncMsgProcessor(ActorSystemContext ctx, LoggingAdapter logger, SessionId sessionId) {
    super(ctx, logger, sessionId);
  }

  @Override
  protected void processToDeviceActorMsg(ActorContext ctx, ToDeviceActorSessionMsg msg) {
    updateSessionCtx(msg, SessionType.ASYNC);
    if (firstMsg) {
      toDeviceMsg(new SessionOpenMsg()).ifPresent(m -> forwardToAppActor(ctx, m));
      firstMsg = false;
    }
    ToDeviceActorMsg pendingMsg = toDeviceMsg(msg);
    FromDeviceMsg fromDeviceMsg = pendingMsg.getPayload();
    switch (fromDeviceMsg.getMsgType()) {
    case POST_TELEMETRY_REQUEST:
    case POST_ATTRIBUTES_REQUEST:
      FromDeviceRequestMsg requestMsg = (FromDeviceRequestMsg) fromDeviceMsg;
      if (requestMsg.getRequestId() >= 0) {
        logger.debug("[{}] Pending request {} registered", requestMsg.getRequestId(), requestMsg.getMsgType());
        // TODO: handle duplicates.
        pendingMap.put(requestMsg.getRequestId(), pendingMsg);
      }
      break;
    case SUBSCRIBE_ATTRIBUTES_REQUEST:
      subscribedToAttributeUpdates = true;
      break;
    case UNSUBSCRIBE_ATTRIBUTES_REQUEST:
      subscribedToAttributeUpdates = false;
      break;
    case SUBSCRIBE_RPC_COMMANDS_REQUEST:
      subscribedToRpcCommands = true;
      break;
    case UNSUBSCRIBE_RPC_COMMANDS_REQUEST:
      subscribedToRpcCommands = false;
      break;
    }
    currentTargetServer = forwardToAppActor(ctx, pendingMsg);
  }

  @Override
  protected void processToAssetActorMsg(ActorContext ctx, ToAssetActorSessionMsg msg) {
    updateSessionCtx(msg, SessionType.ASYNC);
    if (firstMsg) {
      toAssetMsg(new SessionOpenMsg()).ifPresent(m -> forwardToAppActor(ctx, m));
      firstMsg = false;
    }
    ToAssetActorMsg pendingMsg = toAssetMsg(msg);
    FromDeviceMsg fromDeviceMsg = pendingMsg.getPayload();
    switch (fromDeviceMsg.getMsgType()) {
    case POST_TELEMETRY_REQUEST:
    case POST_ATTRIBUTES_REQUEST:
      FromDeviceRequestMsg requestMsg = (FromDeviceRequestMsg) fromDeviceMsg;
      if (requestMsg.getRequestId() >= 0) {
        logger.debug("[{}] Pending request {} registered", requestMsg.getRequestId(), requestMsg.getMsgType());
        // TODO: handle duplicates.
        pendingMap4Asset.put(requestMsg.getRequestId(), pendingMsg);
      }
      break;
    case SUBSCRIBE_ATTRIBUTES_REQUEST:
      subscribedToAttributeUpdates = true;
      break;
    case UNSUBSCRIBE_ATTRIBUTES_REQUEST:
      subscribedToAttributeUpdates = false;
      break;
    case SUBSCRIBE_RPC_COMMANDS_REQUEST:
      subscribedToRpcCommands = true;
      break;
    case UNSUBSCRIBE_RPC_COMMANDS_REQUEST:
      subscribedToRpcCommands = false;
      break;
    }
    currentTargetServer = forwardToAppActor(ctx, pendingMsg);
  }

  @Override
  public void processToDeviceMsg(ActorContext context, ToDeviceMsg msg) {
    try {
      if (msg.getMsgType() != MsgType.SESSION_CLOSE) {
        switch (msg.getMsgType()) {
        case STATUS_CODE_RESPONSE:
        case GET_ATTRIBUTES_RESPONSE:
          ResponseMsg responseMsg = (ResponseMsg) msg;
          if (responseMsg.getRequestId() >= 0) {
            logger.debug("[{}] Pending request processed: {}", responseMsg.getRequestId(), responseMsg);
            pendingMap.remove(responseMsg.getRequestId());
          }
          break;
        }
        sessionCtx.onMsg(new BasicSessionActorToAdaptorMsg(this.sessionCtx, msg));
      } else {
        sessionCtx.onMsg(org.iotp.analytics.ruleengine.common.msg.session.ctrl.SessionCloseMsg
            .onCredentialsRevoked(sessionCtx.getSessionId()));
      }
    } catch (SessionException e) {
      logger.warning("Failed to push session response msg", e);
    }
  }

  @Override
  public void processToAssetMsg(ActorContext context, ToDeviceMsg msg) {
    try {
      if (msg.getMsgType() != MsgType.SESSION_CLOSE) {
        switch (msg.getMsgType()) {
        case STATUS_CODE_RESPONSE:
        case GET_ATTRIBUTES_RESPONSE:
          ResponseMsg responseMsg = (ResponseMsg) msg;
          if (responseMsg.getRequestId() >= 0) {
            logger.debug("[{}] Pending request processed: {}", responseMsg.getRequestId(), responseMsg);
            pendingMap4Asset.remove(responseMsg.getRequestId());
          }
          break;
        }
        sessionCtx.onMsg(new BasicSessionActorToAdaptorMsg(this.sessionCtx, msg));
      } else {
        sessionCtx.onMsg(org.iotp.analytics.ruleengine.common.msg.session.ctrl.SessionCloseMsg
            .onCredentialsRevoked(sessionCtx.getSessionId()));
      }
    } catch (SessionException e) {
      logger.warning("Failed to push session response msg", e);
    }
  }

  @Override
  public void processTimeoutMsg(ActorContext context, SessionTimeoutMsg msg) {
    // TODO Auto-generated method stub
  }

  protected void cleanupSession(ActorContext ctx) {
    toDeviceMsg(new SessionCloseMsg()).ifPresent(m -> forwardToAppActor(ctx, m));
  }

  @Override
  public void processClusterEvent(ActorContext context, ClusterEventMsg msg) {
    if (pendingMap.size() > 0 || subscribedToAttributeUpdates || subscribedToRpcCommands) {
      Optional<ServerAddress> newTargetServer = systemContext.getRoutingService().resolveById(getDeviceId());
      if (!newTargetServer.equals(currentTargetServer)) {
        firstMsg = true;
        currentTargetServer = newTargetServer;
        pendingMap.values().forEach(v -> {
          forwardToAppActor(context, v, currentTargetServer);
          if (currentTargetServer.isPresent()) {
            logger.debug("[{}] Forwarded msg to new server: {}", sessionId, currentTargetServer.get());
          } else {
            logger.debug("[{}] Forwarded msg to local server.", sessionId);
          }
        });
        if (subscribedToAttributeUpdates) {
          toDeviceMsg(new AttributesSubscribeMsg()).ifPresent(m -> forwardToAppActor(context, m, currentTargetServer));
          logger.debug("[{}] Forwarded attributes subscription.", sessionId);
        }
        if (subscribedToRpcCommands) {
          toDeviceMsg(new RpcSubscribeMsg()).ifPresent(m -> forwardToAppActor(context, m, currentTargetServer));
          logger.debug("[{}] Forwarded rpc commands subscription.", sessionId);
        }
      }
    }
    if (pendingMap4Asset.size() > 0 || subscribedToAttributeUpdates || subscribedToRpcCommands) {
      Optional<ServerAddress> newTargetServer = systemContext.getRoutingService().resolveById(getAssetId());
      if (!newTargetServer.equals(currentTargetServer)) {
        firstMsg = true;
        currentTargetServer = newTargetServer;
        pendingMap4Asset.values().forEach(v -> {
          forwardToAppActor(context, v, currentTargetServer);
          if (currentTargetServer.isPresent()) {
            logger.debug("[{}] Forwarded msg to new server: {}", sessionId, currentTargetServer.get());
          } else {
            logger.debug("[{}] Forwarded msg to local server.", sessionId);
          }
        });
        if (subscribedToAttributeUpdates) {
          toAssetMsg(new AttributesSubscribeMsg()).ifPresent(m -> forwardToAppActor(context, m, currentTargetServer));
          logger.debug("[{}] Forwarded attributes subscription.", sessionId);
        }
        if (subscribedToRpcCommands) {
          toAssetMsg(new RpcSubscribeMsg()).ifPresent(m -> forwardToAppActor(context, m, currentTargetServer));
          logger.debug("[{}] Forwarded rpc commands subscription.", sessionId);
        }
      }
    }
  }
}

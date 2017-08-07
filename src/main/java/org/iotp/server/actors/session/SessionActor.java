package org.iotp.server.actors.session;

import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionCtrlMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ToAssetActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ctrl.SessionCloseMsg;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.actors.shared.SessionTimeoutMsg;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

public class SessionActor extends ContextAwareActor {

  private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

  private final SessionId sessionId;
  private AbstractSessionActorMsgProcessor processor;

  private SessionActor(ActorSystemContext systemContext, SessionId sessionId) {
    super(systemContext);
    this.sessionId = sessionId;
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return new OneForOneStrategy(-1, Duration.Inf(), throwable -> {
      logger.error(throwable, "Unknown session error");
      if (throwable instanceof Error) {
        return OneForOneStrategy.escalate();
      } else {
        return OneForOneStrategy.resume();
      }
    });
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    logger.info("[{}] Processing: {}.", sessionId, msg);
    if (msg instanceof ToDeviceActorSessionMsg) {
      processDeviceMsg((ToDeviceActorSessionMsg) msg);
    } else if (msg instanceof ToDeviceSessionActorMsg) {
      processToDeviceMsg((ToDeviceSessionActorMsg) msg);
    } else if (msg instanceof ToAssetActorSessionMsg) {
      processAssetMsg((ToAssetActorSessionMsg) msg);
      // @ TODO
      // } else if (msg instanceof ToAssetSessionActorMsg) {
      // processToAssetMsg((ToAssetSessionActorMsg) msg);
    } else if (msg instanceof SessionTimeoutMsg) {
      processTimeoutMsg((SessionTimeoutMsg) msg);
    } else if (msg instanceof SessionCtrlMsg) {
      processSessionCtrlMsg((SessionCtrlMsg) msg);
    } else if (msg instanceof ClusterEventMsg) {
      processClusterEvent((ClusterEventMsg) msg);
    } else {
      logger.warning("[{}] Unknown msg: {}", sessionId, msg);
    }
  }

  private void processClusterEvent(ClusterEventMsg msg) {
    processor.processClusterEvent(context(), msg);
  }

  private void processDeviceMsg(ToDeviceActorSessionMsg msg) {
    initProcessor(msg.getSessionMsg());
    processor.processToDeviceActorMsg(context(), msg);
  }

  private void processAssetMsg(ToAssetActorSessionMsg msg) {
    initProcessor(msg.getSessionMsg());
    processor.processToAssetActorMsg(context(), msg);
  }

  private void processToDeviceMsg(ToDeviceSessionActorMsg msg) {
    processor.processToDeviceMsg(context(), msg.getMsg());
  }

  private void processTimeoutMsg(SessionTimeoutMsg msg) {
    if (processor != null) {
      processor.processTimeoutMsg(context(), msg);
    } else {
      logger.warning("[{}] Can't process timeout msg: {} without processor", sessionId, msg);
    }
  }

  private void processSessionCtrlMsg(SessionCtrlMsg msg) {
    if (processor != null) {
      processor.processSessionCtrlMsg(context(), msg);
    } else if (msg instanceof SessionCloseMsg) {
      AbstractSessionActorMsgProcessor.terminateSession(context(), sessionId);
    } else {
      logger.warning("[{}] Can't process session ctrl msg: {} without processor", sessionId, msg);
    }
  }

  private void initProcessor(SessionMsg sessionMsg) {
    if (processor == null) {
      // SessionMsg sessionMsg = (SessionMsg) msg.getSessionMsg();
      if (sessionMsg.getSessionContext().getSessionType() == SessionType.SYNC) {
        processor = new SyncMsgProcessor(systemContext, logger, sessionId);
      } else {
        processor = new ASyncMsgProcessor(systemContext, logger, sessionId);
      }
    }
  }

  public static class ActorCreator extends ContextBasedCreator<SessionActor> {
    private static final long serialVersionUID = 1L;

    private final SessionId sessionId;

    public ActorCreator(ActorSystemContext context, SessionId sessionId) {
      super(context);
      this.sessionId = sessionId;
    }

    @Override
    public SessionActor create() throws Exception {
      return new SessionActor(context, sessionId);
    }
  }

}

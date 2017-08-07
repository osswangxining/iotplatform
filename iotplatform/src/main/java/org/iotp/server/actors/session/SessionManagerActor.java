package org.iotp.server.actors.session;

import java.util.HashMap;
import java.util.Map;

import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ClusterEventMsg;
import org.iotp.analytics.ruleengine.common.msg.core.SessionCloseMsg;
import org.iotp.analytics.ruleengine.common.msg.core.ToDeviceSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionCtrlMsg;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;
import org.iotp.server.actors.service.DefaultActorService;
import org.iotp.server.actors.shared.SessionTimeoutMsg;

import akka.actor.ActorRef;
import akka.actor.InvalidActorNameException;
import akka.actor.LocalActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SessionManagerActor extends ContextAwareActor {

  private static final int INITIAL_SESSION_MAP_SIZE = 1024;

  private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  private final Map<String, ActorRef> sessionActors;

  public SessionManagerActor(ActorSystemContext systemContext) {
    super(systemContext);
    this.sessionActors = new HashMap<>(INITIAL_SESSION_MAP_SIZE);
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof SessionAwareMsg) {
      forwardToSessionActor((SessionAwareMsg) msg);
    } else if (msg instanceof SessionTerminationMsg) {
      onSessionTermination((SessionTerminationMsg) msg);
    } else if (msg instanceof Terminated) {
      onTermination((Terminated) msg);
    } else if (msg instanceof SessionTimeoutMsg) {
      onSessionTimeout((SessionTimeoutMsg) msg);
    } else if (msg instanceof SessionCtrlMsg) {
      onSessionCtrlMsg((SessionCtrlMsg) msg);
    } else if (msg instanceof ClusterEventMsg) {
      broadcast(msg);
    }
  }

  private void broadcast(Object msg) {
    sessionActors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
  }

  private void onSessionTimeout(SessionTimeoutMsg msg) {
    String sessionIdStr = msg.getSessionId().toUidStr();
    ActorRef sessionActor = sessionActors.get(sessionIdStr);
    if (sessionActor != null) {
      sessionActor.tell(msg, ActorRef.noSender());
    }
  }

  private void onSessionCtrlMsg(SessionCtrlMsg msg) {
    String sessionIdStr = msg.getSessionId().toUidStr();
    ActorRef sessionActor = sessionActors.get(sessionIdStr);
    if (sessionActor != null) {
      sessionActor.tell(msg, ActorRef.noSender());
    }
  }

  private void onSessionTermination(SessionTerminationMsg msg) {
    String sessionIdStr = msg.getId().toUidStr();
    ActorRef sessionActor = sessionActors.remove(sessionIdStr);
    if (sessionActor != null) {
      log.debug("[{}] Removed session actor.", sessionIdStr);
      // TODO: onSubscriptionUpdate device actor about session close;
    } else {
      log.debug("[{}] Session actor was already removed.", sessionIdStr);
    }
  }

  private void forwardToSessionActor(SessionAwareMsg msg) {
    if (msg instanceof ToDeviceSessionActorMsg || msg instanceof SessionCloseMsg) {
      String sessionIdStr = msg.getSessionId().toUidStr();
      ActorRef sessionActor = sessionActors.get(sessionIdStr);
      if (sessionActor != null) {
        sessionActor.tell(msg, ActorRef.noSender());
      } else {
        log.debug("[{}] Session actor was already removed.", sessionIdStr);
      }
    } else {
      try {
        getOrCreateSessionActor(msg.getSessionId()).tell(msg, self());
      } catch (InvalidActorNameException e) {
        log.info("Invalid msg : {}", msg);
      }
    }
  }

  private ActorRef getOrCreateSessionActor(SessionId sessionId) {
    String sessionIdStr = sessionId.toUidStr();
    ActorRef sessionActor = sessionActors.get(sessionIdStr);
    if (sessionActor == null) {
      log.debug("[{}] Creating session actor.", sessionIdStr);
      sessionActor = context().actorOf(Props.create(new SessionActor.ActorCreator(systemContext, sessionId))
          .withDispatcher(DefaultActorService.SESSION_DISPATCHER_NAME), sessionIdStr);
      sessionActors.put(sessionIdStr, sessionActor);
      log.debug("[{}] Created session actor.", sessionIdStr);
    }
    return sessionActor;
  }

  private void onTermination(Terminated message) {
    ActorRef terminated = message.actor();
    if (terminated instanceof LocalActorRef) {
      log.info("Removed actor: {}.", terminated);
      // TODO: cleanup session actors map
    } else {
      throw new IllegalStateException("Remote actors are not supported!");
    }
  }

  public static class ActorCreator extends ContextBasedCreator<SessionManagerActor> {
    private static final long serialVersionUID = 1L;

    public ActorCreator(ActorSystemContext context) {
      super(context);
    }

    @Override
    public SessionManagerActor create() throws Exception {
      return new SessionManagerActor(context);
    }
  }

}

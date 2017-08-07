package org.iotp.server.actors.rpc;

import java.util.UUID;

import akka.actor.ActorRef;
import lombok.Data;

/**
 */
@Data
public final class SessionActorInfo {
  protected final UUID sessionId;
  protected final ActorRef actor;
}

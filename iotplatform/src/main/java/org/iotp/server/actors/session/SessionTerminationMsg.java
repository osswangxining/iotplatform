package org.iotp.server.actors.session;

import org.iotp.infomgt.data.id.SessionId;
import org.iotp.server.actors.shared.ActorTerminationMsg;

public class SessionTerminationMsg extends ActorTerminationMsg<SessionId> {

  public SessionTerminationMsg(SessionId id) {
    super(id);
  }
}

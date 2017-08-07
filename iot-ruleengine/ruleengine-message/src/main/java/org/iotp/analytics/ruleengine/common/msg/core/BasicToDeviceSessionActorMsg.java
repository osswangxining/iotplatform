package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.infomgt.data.id.SessionId;

public class BasicToDeviceSessionActorMsg implements ToDeviceSessionActorMsg {

  private final ToDeviceMsg msg;
  private final SessionId sessionId;

  public BasicToDeviceSessionActorMsg(ToDeviceMsg msg, SessionId sessionId) {
    super();
    this.msg = msg;
    this.sessionId = sessionId;
  }

  @Override
  public SessionId getSessionId() {
    return sessionId;
  }

  @Override
  public ToDeviceMsg getMsg() {
    return msg;
  }

  @Override
  public String toString() {
    return "BasicToSessionResponseMsg [msg=" + msg + ", sessionId=" + sessionId + "]";
  }

}

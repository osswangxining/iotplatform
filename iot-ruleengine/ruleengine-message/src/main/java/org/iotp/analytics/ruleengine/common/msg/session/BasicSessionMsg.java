package org.iotp.analytics.ruleengine.common.msg.session;

import org.iotp.infomgt.data.id.SessionId;

public class BasicSessionMsg implements SessionMsg {

  private final SessionContext ctx;

  public BasicSessionMsg(SessionContext ctx) {
    super();
    this.ctx = ctx;
  }

  @Override
  public SessionId getSessionId() {
    return ctx.getSessionId();
  }

  @Override
  public SessionContext getSessionContext() {
    return ctx;
  }

  @Override
  public String toString() {
    return "BasicSessionMsg [ctx=" + ctx + "]";
  }

}

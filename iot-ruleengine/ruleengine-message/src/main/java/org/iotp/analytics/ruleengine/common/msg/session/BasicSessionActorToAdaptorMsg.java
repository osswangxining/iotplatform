package org.iotp.analytics.ruleengine.common.msg.session;

public class BasicSessionActorToAdaptorMsg extends BasicSessionMsg implements SessionActorToAdaptorMsg {

  private final ToDeviceMsg msg;

  public BasicSessionActorToAdaptorMsg(SessionContext ctx, ToDeviceMsg msg) {
    super(ctx);
    this.msg = msg;
  }

  @Override
  public ToDeviceMsg getMsg() {
    return msg;
  }

}

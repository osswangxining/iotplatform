package org.iotp.analytics.ruleengine.common.msg.session;

public class BasicAdaptorToSessionActorMsg extends BasicSessionMsg implements AdaptorToSessionActorMsg {

  private final FromDeviceMsg msg;

  public BasicAdaptorToSessionActorMsg(SessionContext ctx, FromDeviceMsg msg) {
    super(ctx);
    this.msg = msg;
  }

  @Override
  public FromDeviceMsg getMsg() {
    return msg;
  }

}

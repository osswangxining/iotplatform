package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

import lombok.ToString;

@ToString
public class SessionCloseNotification implements ToDeviceMsg {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public MsgType getMsgType() {
    return MsgType.SESSION_CLOSE;
  }

}

package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

/**
 */
public class SessionOpenMsg implements FromDeviceMsg {
  @Override
  public MsgType getMsgType() {
    return MsgType.SESSION_OPEN;
  }
}

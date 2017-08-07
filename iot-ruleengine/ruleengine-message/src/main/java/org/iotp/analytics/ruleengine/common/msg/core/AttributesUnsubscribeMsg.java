package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

/**
 */
public class AttributesUnsubscribeMsg implements FromDeviceMsg {
  @Override
  public MsgType getMsgType() {
    return MsgType.UNSUBSCRIBE_ATTRIBUTES_REQUEST;
  }
}

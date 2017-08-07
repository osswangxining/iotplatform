package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

/**
 */
public class RpcSubscribeMsg implements FromDeviceMsg {
  @Override
  public MsgType getMsgType() {
    return MsgType.SUBSCRIBE_RPC_COMMANDS_REQUEST;
  }
}

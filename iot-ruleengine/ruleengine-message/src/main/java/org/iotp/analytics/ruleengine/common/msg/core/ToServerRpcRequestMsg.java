package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.Data;

/**
 */
@Data
public class ToServerRpcRequestMsg implements FromDeviceMsg {

  private final int requestId;
  private final String method;
  private final String params;

  @Override
  public MsgType getMsgType() {
    return MsgType.TO_SERVER_RPC_REQUEST;
  }
}

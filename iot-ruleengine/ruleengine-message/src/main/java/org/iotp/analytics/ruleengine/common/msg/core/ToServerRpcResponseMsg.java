package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

import lombok.Data;

/**
 */
@Data
public class ToServerRpcResponseMsg implements ToDeviceMsg {

  private final int requestId;
  private final String data;

  @Override
  public MsgType getMsgType() {
    return MsgType.TO_SERVER_RPC_RESPONSE;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }
}

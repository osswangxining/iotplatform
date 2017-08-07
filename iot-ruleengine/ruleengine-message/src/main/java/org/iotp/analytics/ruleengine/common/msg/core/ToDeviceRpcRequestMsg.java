package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

import lombok.Data;

/**
 */
@Data
public class ToDeviceRpcRequestMsg implements ToDeviceMsg {

  private final int requestId;
  private final String method;
  private final String params;

  @Override
  public MsgType getMsgType() {
    return MsgType.TO_DEVICE_RPC_REQUEST;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }
}

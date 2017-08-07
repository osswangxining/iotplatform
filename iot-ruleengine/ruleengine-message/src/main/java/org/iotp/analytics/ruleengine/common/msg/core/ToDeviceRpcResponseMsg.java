package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.Data;

/**
 */
@Data
public class ToDeviceRpcResponseMsg implements FromDeviceMsg {

  private final int requestId;
  private final String data;

  @Override
  public MsgType getMsgType() {
    return MsgType.TO_DEVICE_RPC_RESPONSE;
  }
}

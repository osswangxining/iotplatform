package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.kv.AttributesKVMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;

import lombok.ToString;

@ToString
public class AttributesUpdateNotification implements ToDeviceMsg {

  private static final long serialVersionUID = 1L;

  private AttributesKVMsg data;

  public AttributesUpdateNotification(AttributesKVMsg data) {
    this.data = data;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public MsgType getMsgType() {
    return MsgType.ATTRIBUTES_UPDATE_NOTIFICATION;
  }

  public AttributesKVMsg getData() {
    return data;
  }
}

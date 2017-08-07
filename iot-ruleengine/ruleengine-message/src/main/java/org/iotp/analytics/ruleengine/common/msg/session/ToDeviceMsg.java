package org.iotp.analytics.ruleengine.common.msg.session;

import java.io.Serializable;

public interface ToDeviceMsg extends Serializable {

  boolean isSuccess();

  MsgType getMsgType();

}

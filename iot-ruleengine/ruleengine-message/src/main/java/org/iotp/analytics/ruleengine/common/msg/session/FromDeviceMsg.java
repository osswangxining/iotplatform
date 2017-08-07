package org.iotp.analytics.ruleengine.common.msg.session;

import java.io.Serializable;

public interface FromDeviceMsg extends Serializable {

  MsgType getMsgType();

}

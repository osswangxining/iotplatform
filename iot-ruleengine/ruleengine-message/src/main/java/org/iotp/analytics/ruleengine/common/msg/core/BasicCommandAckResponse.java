package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

public class BasicCommandAckResponse extends BasicResponseMsg<Integer> implements StatusCodeResponse {

  private static final long serialVersionUID = 1L;

  public static BasicCommandAckResponse onSuccess(MsgType requestMsgType, Integer requestId) {
    return BasicCommandAckResponse.onSuccess(requestMsgType, requestId, 200);
  }

  public static BasicCommandAckResponse onSuccess(MsgType requestMsgType, Integer requestId, Integer code) {
    return new BasicCommandAckResponse(requestMsgType, requestId, true, null, code);
  }

  public static BasicCommandAckResponse onError(MsgType requestMsgType, Integer requestId, Exception error) {
    return new BasicCommandAckResponse(requestMsgType, requestId, false, error, null);
  }

  private BasicCommandAckResponse(MsgType requestMsgType, Integer requestId, boolean success, Exception error,
      Integer code) {
    super(requestMsgType, requestId, MsgType.TO_DEVICE_RPC_RESPONSE_ACK, success, error, code);
  }

  @Override
  public String toString() {
    return "BasicStatusCodeResponse []";
  }
}

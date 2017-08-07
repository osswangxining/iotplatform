package org.iotp.analytics.ruleengine.common.msg.core;

import org.iotp.analytics.ruleengine.common.msg.kv.AttributesKVMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;

import lombok.ToString;

@ToString
public class BasicGetAttributesResponse extends BasicResponseMsg<AttributesKVMsg> implements GetAttributesResponse {

  private static final long serialVersionUID = 1L;

  public static BasicGetAttributesResponse onSuccess(MsgType requestMsgType, int requestId, AttributesKVMsg code) {
    return new BasicGetAttributesResponse(requestMsgType, requestId, true, null, code);
  }

  public static BasicGetAttributesResponse onError(MsgType requestMsgType, int requestId, Exception error) {
    return new BasicGetAttributesResponse(requestMsgType, requestId, false, error, null);
  }

  private BasicGetAttributesResponse(MsgType requestMsgType, int requestId, boolean success, Exception error,
      AttributesKVMsg code) {
    super(requestMsgType, requestId, MsgType.GET_ATTRIBUTES_RESPONSE, success, error, code);
  }

}

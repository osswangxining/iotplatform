package org.iotp.analytics.ruleengine.common.msg.core;

import java.io.Serializable;

/**
 */
public class BasicRequest implements Serializable {

  public static final Integer DEFAULT_REQUEST_ID = 0;

  private final Integer requestId;

  public BasicRequest(Integer requestId) {
    this.requestId = requestId;
  }

  public Integer getRequestId() {
    return requestId;
  }
}

package org.iotp.server.actors.rule;

import java.io.Serializable;
import java.util.UUID;

public class RuleToPluginTimeoutMsg implements Serializable {

  private static final long serialVersionUID = 1L;

  private final UUID msgId;

  public RuleToPluginTimeoutMsg(UUID msgId) {
    super();
    this.msgId = msgId;
  }

  public UUID getMsgId() {
    return msgId;
  }

}

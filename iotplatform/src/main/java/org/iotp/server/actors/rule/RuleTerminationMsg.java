package org.iotp.server.actors.rule;

import org.iotp.infomgt.data.id.RuleId;
import org.iotp.server.actors.shared.ActorTerminationMsg;

/**
 */
public class RuleTerminationMsg extends ActorTerminationMsg<RuleId> {

  public RuleTerminationMsg(RuleId id) {
    super(id);
  }
}

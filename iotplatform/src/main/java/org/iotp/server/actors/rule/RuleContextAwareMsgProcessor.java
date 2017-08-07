package org.iotp.server.actors.rule;

import org.iotp.infomgt.data.id.RuleId;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.shared.AbstractContextAwareMsgProcessor;

import akka.event.LoggingAdapter;

public class RuleContextAwareMsgProcessor extends AbstractContextAwareMsgProcessor {

  private final RuleId ruleId;

  protected RuleContextAwareMsgProcessor(ActorSystemContext systemContext, LoggingAdapter logger, RuleId ruleId) {
    super(systemContext, logger);
    this.ruleId = ruleId;
  }

}

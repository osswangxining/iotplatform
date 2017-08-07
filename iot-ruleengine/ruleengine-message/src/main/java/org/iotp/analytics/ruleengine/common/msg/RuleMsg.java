package org.iotp.analytics.ruleengine.common.msg;

import org.iotp.analytics.ruleengine.common.msg.aware.RuleAwareMsg;
import org.iotp.infomgt.data.rule.RuleType;
import org.iotp.infomgt.data.rule.Scope;

/**
 * Message that is used to deliver some data to the rule instance. For example:
 * aggregated statistics or command decoded from http request.
 * 
 *
 * @param <V>
 *          - payload
 */
public interface RuleMsg<V> extends RuleAwareMsg {

  Scope getRuleLevel();

  RuleType getRuleType();

  V getPayload();

}

package org.iotp.analytics.ruleengine.plugins.msg;

import java.io.Serializable;
import java.util.UUID;

import org.iotp.analytics.ruleengine.api.rules.ToRuleActorMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;

/**
 * The basic interface for messages that are sent from particular plugin to rule
 * instance
 * 
 *
 */
public interface PluginToRuleMsg<V extends Serializable> extends ToRuleActorMsg, Serializable {

  /**
   * Returns the unique identifier of the message
   * 
   * @return unique identifier of the message.
   */
  UUID getUid();

  /**
   * Returns the unique identifier of the tenant that owns the rule
   *
   * @return unique identifier of the tenant that owns the rule.
   *
   */
  TenantId getTenantId();

  /**
   * Returns the unique identifier of the rule
   * 
   * @return unique identifier of the rule.
   */
  RuleId getRuleId();

  /**
   * Returns the serializable message payload.
   * 
   * @return the serializable message payload.
   */
  V getPayload();

}

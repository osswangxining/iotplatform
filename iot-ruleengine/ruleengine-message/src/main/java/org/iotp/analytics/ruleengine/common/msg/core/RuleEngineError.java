package org.iotp.analytics.ruleengine.common.msg.core;

/**
 */

public enum RuleEngineError {

  NO_RULES, NO_ACTIVE_RULES, NO_FILTERS_MATCHED, NO_REQUEST_FROM_ACTIONS, NO_TWO_WAY_ACTIONS, NO_RESPONSE_FROM_ACTIONS, PLUGIN_TIMEOUT(
      true);

  private final boolean critical;

  RuleEngineError() {
    this(false);
  }

  RuleEngineError(boolean critical) {
    this.critical = critical;
  }

  public boolean isCritical() {
    return critical;
  }

  public int getPriority() {
    return ordinal();
  }
}

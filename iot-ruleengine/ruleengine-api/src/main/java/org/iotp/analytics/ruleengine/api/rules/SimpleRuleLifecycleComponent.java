package org.iotp.analytics.ruleengine.api.rules;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public abstract class SimpleRuleLifecycleComponent implements RuleLifecycleComponent {

  @Override
  public void resume() {
    log.debug("Resume method was called, but no impl provided!");
  }

  @Override
  public void suspend() {
    log.debug("Suspend method was called, but no impl provided!");
  }

  @Override
  public void stop() {
    log.debug("Stop method was called, but no impl provided!");
  }

}

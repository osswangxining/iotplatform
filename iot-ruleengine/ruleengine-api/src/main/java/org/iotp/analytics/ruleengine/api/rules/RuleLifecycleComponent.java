package org.iotp.analytics.ruleengine.api.rules;

/**
 */
public interface RuleLifecycleComponent {

  void resume();

  void suspend();

  void stop();

}

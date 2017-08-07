package org.iotp.analytics.ruleengine.core.filter;

import javax.script.ScriptException;

import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleFilter;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public abstract class BasicJsFilter implements RuleFilter<JsFilterConfiguration> {

  protected JsFilterConfiguration configuration;
  protected NashornJsEvaluator evaluator;

  @Override
  public void init(JsFilterConfiguration configuration) {
    this.configuration = configuration;
    initEvaluator(configuration);
  }

  @Override
  public boolean filter(RuleContext ctx, ToDeviceActorMsg msg) {
    try {
      return doFilter(ctx, msg);
    } catch (ScriptException e) {
      log.warn("RuleFilter evaluation exception: {}", e.getMessage(), e);
      // throw new RuntimeException(e);
      return false;
    }
  }

  protected abstract boolean doFilter(RuleContext ctx, ToDeviceActorMsg msg) throws ScriptException;

  @Override
  public void resume() {
    initEvaluator(configuration);
  }

  @Override
  public void suspend() {
    destroyEvaluator();
  }

  @Override
  public void stop() {
    destroyEvaluator();
  }

  private void initEvaluator(JsFilterConfiguration configuration) {
    evaluator = new NashornJsEvaluator(configuration.getFilter());
  }

  private void destroyEvaluator() {
    if (evaluator != null) {
      evaluator.destroy();
    }
  }

}

package org.iotp.analytics.ruleengine.annotation;

/**
 */
public interface ConfigurableComponent<T> {

  void init(T configuration);

}

package org.iotp.analytics.ruleengine.configuration;

public interface Configurable<C extends Configuration> {

  Class<C> getConfigurationClass();

  void validate(C configuration) throws ConfigurationValidationException;

  void configure(C configuration);

}

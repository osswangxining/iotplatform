package org.iotp.analytics.ruleengine.api.plugins;

/**
 */
public interface PluginCallback<T> {

  void onSuccess(PluginContext ctx, T value);

  void onFailure(PluginContext ctx, Exception e);
}

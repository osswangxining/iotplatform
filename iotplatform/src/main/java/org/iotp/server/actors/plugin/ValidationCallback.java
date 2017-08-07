package org.iotp.server.actors.plugin;

import org.iotp.analytics.ruleengine.api.plugins.PluginCallback;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.exception.UnauthorizedException;

import com.hazelcast.util.function.Consumer;

/**
 */
public class ValidationCallback implements PluginCallback<Boolean> {

  private final PluginCallback<?> callback;
  private final Consumer<PluginContext> action;

  public ValidationCallback(PluginCallback<?> callback, Consumer<PluginContext> action) {
    this.callback = callback;
    this.action = action;
  }

  @Override
  public void onSuccess(PluginContext ctx, Boolean value) {
    if (value) {
      action.accept(ctx);
    } else {
      onFailure(ctx, new UnauthorizedException("Permission denied."));
    }
  }

  @Override
  public void onFailure(PluginContext ctx, Exception e) {
    callback.onFailure(ctx, e);
  }
}

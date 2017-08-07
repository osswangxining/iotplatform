package org.iotp.analytics.ruleengine.core.plugin.telemetry.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginCallback;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public abstract class BiPluginCallBack<V1, V2> {

  private V1 v1;
  private V2 v2;

  public PluginCallback<V1> getV1Callback() {
    return new PluginCallback<V1>() {
      @Override
      public void onSuccess(PluginContext ctx, V1 value) {
        synchronized (BiPluginCallBack.this) {
          BiPluginCallBack.this.v1 = value;
          if (v2 != null) {
            BiPluginCallBack.this.onSuccess(ctx, v1, v2);
          }
        }
      }

      @Override
      public void onFailure(PluginContext ctx, Exception e) {
        BiPluginCallBack.this.onFailure(ctx, e);
      }
    };
  }

  public PluginCallback<V2> getV2Callback() {
    return new PluginCallback<V2>() {
      @Override
      public void onSuccess(PluginContext ctx, V2 value) {
        synchronized (BiPluginCallBack.this) {
          BiPluginCallBack.this.v2 = value;
          if (v1 != null) {
            BiPluginCallBack.this.onSuccess(ctx, v1, v2);
          }
        }

      }

      @Override
      public void onFailure(PluginContext ctx, Exception e) {
        BiPluginCallBack.this.onFailure(ctx, e);
      }
    };
  }

  abstract public void onSuccess(PluginContext ctx, V1 v1, V2 v2);

  abstract public void onFailure(PluginContext ctx, Exception e);

}

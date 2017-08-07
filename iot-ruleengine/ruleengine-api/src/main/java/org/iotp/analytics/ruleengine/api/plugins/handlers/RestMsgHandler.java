package org.iotp.analytics.ruleengine.api.plugins.handlers;

import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.plugins.rest.PluginRestMsg;

/**
 */
public interface RestMsgHandler {

  void process(PluginContext ctx, PluginRestMsg msg);

}

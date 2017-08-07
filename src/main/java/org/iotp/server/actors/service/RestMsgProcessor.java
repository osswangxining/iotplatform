package org.iotp.server.actors.service;

import org.iotp.analytics.ruleengine.plugins.rest.PluginRestMsg;

public interface RestMsgProcessor {

    void process(PluginRestMsg msg);

}

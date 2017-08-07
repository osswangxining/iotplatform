package org.iotp.analytics.ruleengine.plugins.msg;

import org.iotp.analytics.ruleengine.common.msg.aware.PluginAwareMsg;
import org.iotp.infomgt.data.id.TenantId;

public interface ToPluginActorMsg extends PluginAwareMsg {

  TenantId getPluginTenantId();

}

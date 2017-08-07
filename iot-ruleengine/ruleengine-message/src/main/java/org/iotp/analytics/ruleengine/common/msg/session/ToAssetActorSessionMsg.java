package org.iotp.analytics.ruleengine.common.msg.session;

import org.iotp.analytics.ruleengine.common.msg.aware.AssetAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.CustomerAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.SessionAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;

public interface ToAssetActorSessionMsg extends AssetAwareMsg, CustomerAwareMsg, TenantAwareMsg, SessionAwareMsg {

  AdaptorToSessionActorMsg getSessionMsg();

}

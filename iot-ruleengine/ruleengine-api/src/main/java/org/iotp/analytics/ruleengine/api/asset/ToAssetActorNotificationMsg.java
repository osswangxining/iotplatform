package org.iotp.analytics.ruleengine.api.asset;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.common.msg.aware.AssetAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;

/**
 */
public interface ToAssetActorNotificationMsg extends TenantAwareMsg, AssetAwareMsg, Serializable {

}

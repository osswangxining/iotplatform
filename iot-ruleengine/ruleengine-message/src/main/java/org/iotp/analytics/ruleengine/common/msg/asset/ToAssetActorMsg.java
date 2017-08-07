package org.iotp.analytics.ruleengine.common.msg.asset;

import java.io.Serializable;
import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.aware.AssetAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.CustomerAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.aware.TenantAwareMsg;
import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.infomgt.data.id.SessionId;

public interface ToAssetActorMsg extends AssetAwareMsg, CustomerAwareMsg, TenantAwareMsg, Serializable {

    SessionId getSessionId();

    SessionType getSessionType();

    Optional<ServerAddress> getServerAddress();
    
    FromDeviceMsg getPayload();

    ToAssetActorMsg toOtherAddress(ServerAddress otherAddress);
}

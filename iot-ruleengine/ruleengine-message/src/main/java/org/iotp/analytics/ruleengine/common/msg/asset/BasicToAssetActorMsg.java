package org.iotp.analytics.ruleengine.common.msg.asset;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionType;
import org.iotp.analytics.ruleengine.common.msg.session.ToAssetActorSessionMsg;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.ToString;

@ToString
public class BasicToAssetActorMsg implements ToAssetActorMsg {

    private static final long serialVersionUID = -1866795134993115408L;

    private final TenantId tenantId;
    private final CustomerId customerId;
    private final AssetId assetId;
    private final SessionId sessionId;
    private final SessionType sessionType;
    private final ServerAddress serverAddress;
    private final FromDeviceMsg msg;

    public BasicToAssetActorMsg(ToAssetActorMsg other, FromDeviceMsg msg) {
        this(null, other.getTenantId(), other.getCustomerId(), other.getAssetId(), other.getSessionId(), other.getSessionType(), msg);
    }

    public BasicToAssetActorMsg(ToAssetActorSessionMsg msg, SessionType sessionType) {
        this(null, msg.getTenantId(), msg.getCustomerId(), msg.getAssetId(), msg.getSessionId(), sessionType, msg.getSessionMsg().getMsg());
    }

    private BasicToAssetActorMsg(ServerAddress serverAddress, TenantId tenantId, CustomerId customerId, AssetId assetId, SessionId sessionId, SessionType sessionType,
        FromDeviceMsg msg) {
        super();
        this.serverAddress = serverAddress;
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.assetId = assetId;
        this.sessionId = sessionId;
        this.sessionType = sessionType;
        this.msg = msg;
    }

    @Override
    public AssetId getAssetId() {
        return assetId;
    }

    @Override
    public CustomerId getCustomerId() {
        return customerId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    @Override
    public SessionId getSessionId() {
        return sessionId;
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public Optional<ServerAddress> getServerAddress() {
        return Optional.ofNullable(serverAddress);
    }

    @Override
    public FromDeviceMsg getPayload() {
        return msg;
    }

    @Override
    public ToAssetActorMsg toOtherAddress(ServerAddress otherAddress) {
        return new BasicToAssetActorMsg(otherAddress, tenantId, customerId, assetId, sessionId, sessionType, msg);
    }
}

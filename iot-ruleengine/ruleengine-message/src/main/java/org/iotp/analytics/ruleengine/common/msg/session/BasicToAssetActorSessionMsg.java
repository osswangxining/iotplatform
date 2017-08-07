package org.iotp.analytics.ruleengine.common.msg.session;

import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.SessionId;
import org.iotp.infomgt.data.id.TenantId;

public class BasicToAssetActorSessionMsg implements ToAssetActorSessionMsg {

  private final TenantId tenantId;
  private final CustomerId customerId;
  private final AssetId assetId;
  private final AdaptorToSessionActorMsg msg;

  public BasicToAssetActorSessionMsg(Asset asset, AdaptorToSessionActorMsg msg) {
    super();
    this.tenantId = asset.getTenantId();
    this.customerId = asset.getCustomerId();
    this.assetId = asset.getId();
    this.msg = msg;
  }

  public BasicToAssetActorSessionMsg(ToAssetActorSessionMsg assetMsg) {
    this.tenantId = assetMsg.getTenantId();
    this.customerId = assetMsg.getCustomerId();
    this.assetId = assetMsg.getAssetId();
    this.msg = assetMsg.getSessionMsg();
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
    return msg.getSessionId();
  }

  @Override
  public AdaptorToSessionActorMsg getSessionMsg() {
    return msg;
  }

  @Override
  public String toString() {
    return "BasicToAssetActorSessionMsg [tenantId=" + tenantId + ", customerId=" + customerId + ", assetId=" + assetId
        + ", msg=" + msg + "]";
  }

}

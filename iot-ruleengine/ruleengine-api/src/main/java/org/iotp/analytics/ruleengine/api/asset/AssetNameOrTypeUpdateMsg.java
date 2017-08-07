package org.iotp.analytics.ruleengine.api.asset;

import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetNameOrTypeUpdateMsg implements ToAssetActorNotificationMsg {
  /**
  * 
  */
  private static final long serialVersionUID = 1501846863754429773L;
  private final TenantId tenantId;
  private final AssetId assetId;
  private final String deviceName;
  private final String deviceType;
}

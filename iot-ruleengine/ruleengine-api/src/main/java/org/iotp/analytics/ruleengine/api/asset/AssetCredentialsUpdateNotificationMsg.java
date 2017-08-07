package org.iotp.analytics.ruleengine.api.asset;

import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.Data;

/**
 */
@Data
public class AssetCredentialsUpdateNotificationMsg implements ToAssetActorNotificationMsg {

  /**
  * 
  */
  private static final long serialVersionUID = -1796113638158672428L;
  private final TenantId tenantId;
  private final AssetId assetId;

}

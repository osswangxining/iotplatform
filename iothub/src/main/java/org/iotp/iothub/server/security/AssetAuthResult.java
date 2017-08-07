package org.iotp.iothub.server.security;

import org.iotp.infomgt.data.id.AssetId;

public class AssetAuthResult {

  private final boolean success;
  private final AssetId assetId;
  private final String errorMsg;

  public static AssetAuthResult of(AssetId assetId) {
    return new AssetAuthResult(true, assetId, null);
  }

  public static AssetAuthResult of(String errorMsg) {
    return new AssetAuthResult(false, null, errorMsg);
  }

  private AssetAuthResult(boolean success, AssetId assetId, String errorMsg) {
    super();
    this.success = success;
    this.assetId = assetId;
    this.errorMsg = errorMsg;
  }

  public boolean isSuccess() {
    return success;
  }

  public AssetId getAssetId() {
    return assetId;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  @Override
  public String toString() {
    return "AssetAuthResult [success=" + success + ", assetId=" + assetId + ", errorMsg=" + errorMsg + "]";
  }

}

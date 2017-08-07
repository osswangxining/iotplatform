package org.iotp.infomgt.data.security;

import org.iotp.infomgt.data.common.BaseData;
import org.iotp.infomgt.data.id.AssetCredentialsId;
import org.iotp.infomgt.data.id.AssetId;

public class AssetCredentials extends BaseData<AssetCredentialsId> implements AssetCredentialsFilter {

  private static final long serialVersionUID = -7869261127032837765L;

  private AssetId assetId;
  private AssetCredentialsType credentialsType;
  private String credentialsId;
  private String credentialsValue;

  public AssetCredentials() {
    super();
  }

  public AssetCredentials(AssetCredentialsId id) {
    super(id);
  }

  public AssetCredentials(AssetCredentials assetCredentials) {
    super(assetCredentials);
    this.assetId = assetCredentials.getAssetId();
    this.credentialsType = assetCredentials.getCredentialsType();
    this.credentialsId = assetCredentials.getCredentialsId();
    this.credentialsValue = assetCredentials.getCredentialsValue();
  }

  public AssetId getAssetId() {
    return this.assetId;
  }

  public void setAssetId(AssetId assetId) {
    this.assetId = assetId;
  }

  @Override
  public AssetCredentialsType getCredentialsType() {
    return credentialsType;
  }

  public void setCredentialsType(AssetCredentialsType credentialsType) {
    this.credentialsType = credentialsType;
  }

  @Override
  public String getCredentialsId() {
    return credentialsId;
  }

  public void setCredentialsId(String credentialsId) {
    this.credentialsId = credentialsId;
  }

  public String getCredentialsValue() {
    return credentialsValue;
  }

  public void setCredentialsValue(String credentialsValue) {
    this.credentialsValue = credentialsValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((credentialsId == null) ? 0 : credentialsId.hashCode());
    result = prime * result + ((credentialsType == null) ? 0 : credentialsType.hashCode());
    result = prime * result + ((credentialsValue == null) ? 0 : credentialsValue.hashCode());
    result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AssetCredentials other = (AssetCredentials) obj;
    if (credentialsId == null) {
      if (other.credentialsId != null)
        return false;
    } else if (!credentialsId.equals(other.credentialsId))
      return false;
    if (credentialsType != other.credentialsType)
      return false;
    if (credentialsValue == null) {
      if (other.credentialsValue != null)
        return false;
    } else if (!credentialsValue.equals(other.credentialsValue))
      return false;
    if (assetId == null) {
      if (other.assetId != null)
        return false;
    } else if (!assetId.equals(other.assetId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AssetCredentials [assetId=" + assetId + ", credentialsType=" + credentialsType + ", credentialsId="
        + credentialsId + ", credentialsValue=" + credentialsValue + ", createdTime=" + createdTime + ", id=" + id
        + "]";
  }

}

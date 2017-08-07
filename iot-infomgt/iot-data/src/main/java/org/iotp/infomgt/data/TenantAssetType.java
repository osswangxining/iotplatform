package org.iotp.infomgt.data;

import java.util.UUID;

import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.id.TenantId;

public class TenantAssetType implements java.io.Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = -4467166362415491047L;
  private String type;
  private TenantId tenantId;

  public TenantAssetType() {
    super();
  }

  public TenantAssetType(String type, TenantId tenantId) {
    this.type = type;
    this.tenantId = tenantId;
  }

  public TenantAssetType(String type, UUID tenantId) {
    this.type = type;
    this.tenantId = new TenantId(tenantId);
  }

  public TenantAssetType(String type, String tenantId) {
    this.type = type;
    this.tenantId = new TenantId(UUIDConverter.fromString(tenantId));
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    TenantAssetType that = (TenantAssetType) o;

    if (type != null ? !type.equals(that.type) : that.type != null)
      return false;
    return tenantId != null ? tenantId.equals(that.tenantId) : that.tenantId == null;

  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("TenantAssetType{");
    sb.append("type='").append(type).append('\'');
    sb.append(", tenantId=").append(tenantId);
    sb.append('}');
    return sb.toString();
  }
}

package org.iotp.infomgt.data;

import org.iotp.infomgt.data.id.TenantId;

public class TenantDeviceType implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -1368485994486675642L;
  private String type;
  private TenantId tenantId;

  public TenantDeviceType() {
    super();
  }

  public TenantDeviceType(String type, TenantId tenantId) {
    this.type = type;
    this.tenantId = tenantId;
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

    TenantDeviceType that = (TenantDeviceType) o;

    if (type != null ? !type.equals(that.type) : that.type != null)
      return false;
    return tenantId != null ? tenantId.equals(that.tenantId) : that.tenantId == null;

  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("TenantDeviceType{");
    sb.append("type='").append(type).append('\'');
    sb.append(", tenantId=").append(tenantId);
    sb.append('}');
    return sb.toString();
  }
}

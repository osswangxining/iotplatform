package org.iotp.infomgt.data;

import org.iotp.infomgt.data.common.NamingThing;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DashboardId;
import org.iotp.infomgt.data.id.TenantId;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardInfo extends SearchTextBased<DashboardId> implements NamingThing {

  /**
   * 
   */
  private static final long serialVersionUID = -5998181085177493691L;
  private TenantId tenantId;
  private CustomerId customerId;
  private String title;

  public DashboardInfo() {
    super();
  }

  public DashboardInfo(DashboardId id) {
    super(id);
  }

  public DashboardInfo(DashboardInfo dashboardInfo) {
    super(dashboardInfo);
    this.tenantId = dashboardInfo.getTenantId();
    this.customerId = dashboardInfo.getCustomerId();
    this.title = dashboardInfo.getTitle();
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public void setCustomerId(CustomerId customerId) {
    this.customerId = customerId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public String getName() {
    return title;
  }

  @Override
  public String getSearchText() {
    return title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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
    DashboardInfo other = (DashboardInfo) obj;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DashboardInfo [tenantId=");
    builder.append(tenantId);
    builder.append(", customerId=");
    builder.append(customerId);
    builder.append(", title=");
    builder.append(title);
    builder.append("]");
    return builder.toString();
  }

}

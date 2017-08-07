package org.iotp.infomgt.data;

import org.iotp.infomgt.data.id.DashboardId;

import com.fasterxml.jackson.databind.JsonNode;

public class Dashboard extends DashboardInfo {

  private static final long serialVersionUID = 872682138346187503L;

  private JsonNode configuration;

  public Dashboard() {
    super();
  }

  public Dashboard(DashboardId id) {
    super(id);
  }

  public Dashboard(DashboardInfo dashboardInfo) {
    super(dashboardInfo);
  }

  public Dashboard(Dashboard dashboard) {
    super(dashboard);
    this.configuration = dashboard.getConfiguration();
  }

  public JsonNode getConfiguration() {
    return configuration;
  }

  public void setConfiguration(JsonNode configuration) {
    this.configuration = configuration;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
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
    Dashboard other = (Dashboard) obj;
    if (configuration == null) {
      if (other.configuration != null)
        return false;
    } else if (!configuration.equals(other.configuration))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Dashboard [tenantId=");
    builder.append(getTenantId());
    builder.append(", customerId=");
    builder.append(getCustomerId());
    builder.append(", title=");
    builder.append(getTitle());
    builder.append(", configuration=");
    builder.append(configuration);
    builder.append("]");
    return builder.toString();
  }
}

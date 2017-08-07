package org.iotp.infomgt.data.widget;

import org.iotp.infomgt.data.common.BaseData;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.id.WidgetTypeId;

import com.fasterxml.jackson.databind.JsonNode;

public class WidgetType extends BaseData<WidgetTypeId> {

  /**
   * 
   */
  private static final long serialVersionUID = 7955044526643024160L;
  private TenantId tenantId;
  private String bundleAlias;
  private String alias;
  private String name;
  private JsonNode descriptor;

  public WidgetType() {
    super();
  }

  public WidgetType(WidgetTypeId id) {
    super(id);
  }

  public WidgetType(WidgetType widgetType) {
    super(widgetType);
    this.tenantId = widgetType.getTenantId();
    this.bundleAlias = widgetType.getBundleAlias();
    this.alias = widgetType.getAlias();
    this.name = widgetType.getName();
    this.descriptor = widgetType.getDescriptor();
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  public String getBundleAlias() {
    return bundleAlias;
  }

  public void setBundleAlias(String bundleAlias) {
    this.bundleAlias = bundleAlias;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public JsonNode getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(JsonNode descriptor) {
    this.descriptor = descriptor;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
    result = 31 * result + (bundleAlias != null ? bundleAlias.hashCode() : 0);
    result = 31 * result + (alias != null ? alias.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (descriptor != null ? descriptor.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    WidgetType that = (WidgetType) o;

    if (tenantId != null ? !tenantId.equals(that.tenantId) : that.tenantId != null)
      return false;
    if (bundleAlias != null ? !bundleAlias.equals(that.bundleAlias) : that.bundleAlias != null)
      return false;
    if (alias != null ? !alias.equals(that.alias) : that.alias != null)
      return false;
    if (name != null ? !name.equals(that.name) : that.name != null)
      return false;
    return descriptor != null ? descriptor.equals(that.descriptor) : that.descriptor == null;

  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("WidgetType{");
    sb.append("tenantId=").append(tenantId);
    sb.append(", bundleAlias='").append(bundleAlias).append('\'');
    sb.append(", alias='").append(alias).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", descriptor=").append(descriptor);
    sb.append('}');
    return sb.toString();
  }

}

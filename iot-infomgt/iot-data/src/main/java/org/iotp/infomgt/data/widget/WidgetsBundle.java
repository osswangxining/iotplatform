package org.iotp.infomgt.data.widget;

import java.util.Arrays;

import org.iotp.infomgt.data.SearchTextBased;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.id.WidgetsBundleId;

public class WidgetsBundle extends SearchTextBased<WidgetsBundleId> {

  /**
   * 
   */
  private static final long serialVersionUID = 2311158087685354102L;
  private TenantId tenantId;
  private String alias;
  private String title;
  private byte[] image;

  public WidgetsBundle() {
    super();
  }

  public WidgetsBundle(WidgetsBundleId id) {
    super(id);
  }

  public WidgetsBundle(WidgetsBundle widgetsBundle) {
    super(widgetsBundle);
    this.tenantId = widgetsBundle.getTenantId();
    this.alias = widgetsBundle.getAlias();
    this.title = widgetsBundle.getTitle();
    this.image = widgetsBundle.getImage();
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  @Override
  public String getSearchText() {
    return title;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
    result = 31 * result + (alias != null ? alias.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(image);
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

    WidgetsBundle that = (WidgetsBundle) o;

    if (tenantId != null ? !tenantId.equals(that.tenantId) : that.tenantId != null)
      return false;
    if (alias != null ? !alias.equals(that.alias) : that.alias != null)
      return false;
    if (title != null ? !title.equals(that.title) : that.title != null)
      return false;
    return Arrays.equals(image, that.image);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("WidgetsBundle{");
    sb.append("tenantId=").append(tenantId);
    sb.append(", alias='").append(alias).append('\'');
    sb.append(", title='").append(title).append('\'');
    sb.append(", image=").append(Arrays.toString(image));
    sb.append('}');
    return sb.toString();
  }

}

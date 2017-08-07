package org.iotp.infomgt.data;

import org.iotp.infomgt.data.common.NamingThing;
import org.iotp.infomgt.data.id.TenantId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Tenant extends ContactBased<TenantId> implements NamingThing {

  private static final long serialVersionUID = 8057243243859922101L;

  private String title;
  private String region;
  private JsonNode additionalInfo;

  public Tenant() {
    super();
  }

  public Tenant(TenantId id) {
    super(id);
  }

  public Tenant(Tenant tenant) {
    super(tenant);
    this.title = tenant.getTitle();
    this.region = tenant.getRegion();
    this.additionalInfo = tenant.getAdditionalInfo();
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

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public JsonNode getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(JsonNode additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Override
  public String getSearchText() {
    return title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((additionalInfo == null) ? 0 : additionalInfo.hashCode());
    result = prime * result + ((region == null) ? 0 : region.hashCode());
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
    Tenant other = (Tenant) obj;
    if (additionalInfo == null) {
      if (other.additionalInfo != null)
        return false;
    } else if (!additionalInfo.equals(other.additionalInfo))
      return false;
    if (region == null) {
      if (other.region != null)
        return false;
    } else if (!region.equals(other.region))
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
    builder.append("Tenant [title=");
    builder.append(title);
    builder.append(", region=");
    builder.append(region);
    builder.append(", additionalInfo=");
    builder.append(additionalInfo);
    builder.append(", country=");
    builder.append(country);
    builder.append(", state=");
    builder.append(state);
    builder.append(", city=");
    builder.append(city);
    builder.append(", address=");
    builder.append(address);
    builder.append(", address2=");
    builder.append(address2);
    builder.append(", zip=");
    builder.append(zip);
    builder.append(", phone=");
    builder.append(phone);
    builder.append(", email=");
    builder.append(email);
    builder.append(", createdTime=");
    builder.append(createdTime);
    builder.append(", id=");
    builder.append(id);
    builder.append("]");
    return builder.toString();
  }

}

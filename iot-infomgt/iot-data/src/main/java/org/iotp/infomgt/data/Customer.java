package org.iotp.infomgt.data;

import org.iotp.infomgt.data.common.NamingThing;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.TenantId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.JsonNode;

public class Customer extends ContactBased<CustomerId> implements NamingThing {

  private static final long serialVersionUID = -1599722990298929275L;

  private String title;
  private TenantId tenantId;
  private JsonNode additionalInfo;

  public Customer() {
    super();
  }

  public Customer(CustomerId id) {
    super(id);
  }

  public Customer(Customer customer) {
    super(customer);
    this.tenantId = customer.getTenantId();
    this.title = customer.getTitle();
    this.additionalInfo = customer.getAdditionalInfo();
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  @JsonProperty(access = Access.READ_ONLY)
  public String getName() {
    return title;
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
    Customer other = (Customer) obj;
    if (additionalInfo == null) {
      if (other.additionalInfo != null)
        return false;
    } else if (!additionalInfo.equals(other.additionalInfo))
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
    builder.append("Customer [title=");
    builder.append(title);
    builder.append(", tenantId=");
    builder.append(tenantId);
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

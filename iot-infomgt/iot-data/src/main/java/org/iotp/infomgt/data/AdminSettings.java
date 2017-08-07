package org.iotp.infomgt.data;

import org.iotp.infomgt.data.common.BaseData;
import org.iotp.infomgt.data.id.AdminSettingsId;

import com.fasterxml.jackson.databind.JsonNode;

public class AdminSettings extends BaseData<AdminSettingsId> {

  private static final long serialVersionUID = -7670322981725511892L;

  private String key;
  private JsonNode jsonValue;

  public AdminSettings() {
    super();
  }

  public AdminSettings(AdminSettingsId id) {
    super(id);
  }

  public AdminSettings(AdminSettings adminSettings) {
    super(adminSettings);
    this.key = adminSettings.getKey();
    this.jsonValue = adminSettings.getJsonValue();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public JsonNode getJsonValue() {
    return jsonValue;
  }

  public void setJsonValue(JsonNode jsonValue) {
    this.jsonValue = jsonValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((jsonValue == null) ? 0 : jsonValue.hashCode());
    result = prime * result + ((key == null) ? 0 : key.hashCode());
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
    AdminSettings other = (AdminSettings) obj;
    if (jsonValue == null) {
      if (other.jsonValue != null)
        return false;
    } else if (!jsonValue.equals(other.jsonValue))
      return false;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdminSettings [key=");
    builder.append(key);
    builder.append(", jsonValue=");
    builder.append(jsonValue);
    builder.append(", createdTime=");
    builder.append(createdTime);
    builder.append(", id=");
    builder.append(id);
    builder.append("]");
    return builder.toString();
  }

}

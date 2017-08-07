package org.iotp.infomgt.data.plugin;

import org.iotp.infomgt.data.SearchTextBased;
import org.iotp.infomgt.data.common.NamingThing;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;

import com.fasterxml.jackson.databind.JsonNode;

public class PluginMetaData extends SearchTextBased<PluginId> implements NamingThing {

  private static final long serialVersionUID = 1L;

  private String apiToken;
  private TenantId tenantId;
  private String name;
  private String clazz;
  private boolean publicAccess;
  private ComponentLifecycleState state;
  private JsonNode configuration;
  private JsonNode additionalInfo;

  public PluginMetaData() {
    super();
  }

  public PluginMetaData(PluginId id) {
    super(id);
  }

  public PluginMetaData(PluginMetaData plugin) {
    super(plugin);
    this.apiToken = plugin.getApiToken();
    this.tenantId = plugin.getTenantId();
    this.name = plugin.getName();
    this.clazz = plugin.getClazz();
    this.publicAccess = plugin.isPublicAccess();
    this.state = plugin.getState();
    this.configuration = plugin.getConfiguration();
    this.additionalInfo = plugin.getAdditionalInfo();
  }

  @Override
  public String getSearchText() {
    return name;
  }

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  public TenantId getTenantId() {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId) {
    this.tenantId = tenantId;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public JsonNode getConfiguration() {
    return configuration;
  }

  public void setConfiguration(JsonNode configuration) {
    this.configuration = configuration;
  }

  public boolean isPublicAccess() {
    return publicAccess;
  }

  public void setPublicAccess(boolean publicAccess) {
    this.publicAccess = publicAccess;
  }

  public void setState(ComponentLifecycleState state) {
    this.state = state;
  }

  public ComponentLifecycleState getState() {
    return state;
  }

  public JsonNode getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(JsonNode additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((apiToken == null) ? 0 : apiToken.hashCode());
    result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
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
    PluginMetaData other = (PluginMetaData) obj;
    if (apiToken == null) {
      if (other.apiToken != null)
        return false;
    } else if (!apiToken.equals(other.apiToken))
      return false;
    if (clazz == null) {
      if (other.clazz != null)
        return false;
    } else if (!clazz.equals(other.clazz))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "PluginMetaData [apiToken=" + apiToken + ", tenantId=" + tenantId + ", name=" + name + ", clazz=" + clazz
        + ", publicAccess=" + publicAccess + ", configuration=" + configuration + "]";
  }

}

package org.iotp.infomgt.data.plugin;

import org.iotp.infomgt.data.SearchTextBased;
import org.iotp.infomgt.data.id.ComponentDescriptorId;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ComponentDescriptor extends SearchTextBased<ComponentDescriptorId> {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private ComponentType type;
  @Getter
  @Setter
  private ComponentScope scope;
  @Getter
  @Setter
  private String name;
  @Getter
  @Setter
  private String clazz;
  @Getter
  @Setter
  private JsonNode configurationDescriptor;
  @Getter
  @Setter
  private String actions;

  public ComponentDescriptor() {
    super();
  }

  public ComponentDescriptor(ComponentDescriptorId id) {
    super(id);
  }

  public ComponentDescriptor(ComponentDescriptor plugin) {
    super(plugin);
    this.type = plugin.getType();
    this.scope = plugin.getScope();
    this.name = plugin.getName();
    this.clazz = plugin.getClazz();
    this.configurationDescriptor = plugin.getConfigurationDescriptor();
    this.actions = plugin.getActions();
  }

  @Override
  public String getSearchText() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ComponentDescriptor that = (ComponentDescriptor) o;

    if (type != that.type)
      return false;
    if (scope != that.scope)
      return false;
    if (name != null ? !name.equals(that.name) : that.name != null)
      return false;
    if (actions != null ? !actions.equals(that.actions) : that.actions != null)
      return false;
    if (configurationDescriptor != null ? !configurationDescriptor.equals(that.configurationDescriptor)
        : that.configurationDescriptor != null)
      return false;
    return clazz != null ? clazz.equals(that.clazz) : that.clazz == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (scope != null ? scope.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
    result = 31 * result + (actions != null ? actions.hashCode() : 0);
    return result;
  }
}

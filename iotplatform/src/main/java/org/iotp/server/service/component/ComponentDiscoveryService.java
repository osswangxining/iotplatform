package org.iotp.server.service.component;

import java.util.List;
import java.util.Optional;

import org.iotp.infomgt.data.plugin.ComponentDescriptor;
import org.iotp.infomgt.data.plugin.ComponentType;

/**
 */
public interface ComponentDiscoveryService {

  void discoverComponents();

  List<ComponentDescriptor> getComponents(ComponentType type);

  Optional<ComponentDescriptor> getComponent(String clazz);

  List<ComponentDescriptor> getPluginActions(String pluginClazz);

}

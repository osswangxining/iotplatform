package org.iotp.server.controller;

import java.util.List;

import org.iotp.infomgt.data.plugin.ComponentDescriptor;
import org.iotp.infomgt.data.plugin.ComponentType;
import org.iotp.server.exception.IoTPException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ComponentDescriptorController extends BaseController {

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','TENANT_ADMIN')")
    @RequestMapping(value = "/component/{componentDescriptorClazz:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ComponentDescriptor getComponentDescriptorByClazz(@PathVariable("componentDescriptorClazz") String strComponentDescriptorClazz) throws IoTPException {
        checkParameter("strComponentDescriptorClazz", strComponentDescriptorClazz);
        try {
            return checkComponentDescriptorByClazz(strComponentDescriptorClazz);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','TENANT_ADMIN')")
    @RequestMapping(value = "/components/{componentType}", method = RequestMethod.GET)
    @ResponseBody
    public List<ComponentDescriptor> getComponentDescriptorsByType(@PathVariable("componentType") String strComponentType) throws IoTPException {
        checkParameter("componentType", strComponentType);
        try {
            return checkComponentDescriptorsByType(ComponentType.valueOf(strComponentType));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','TENANT_ADMIN')")
    @RequestMapping(value = "/components/actions/{pluginClazz:.+}", method = RequestMethod.GET)
    @ResponseBody
    public List<ComponentDescriptor> getPluginActionsByPluginClazz(@PathVariable("pluginClazz") String pluginClazz) throws IoTPException {
        checkParameter("pluginClazz", pluginClazz);
        try {
            return checkPluginActionsByPluginClazz(pluginClazz);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}

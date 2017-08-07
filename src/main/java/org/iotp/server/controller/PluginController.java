package org.iotp.server.controller;

import java.util.List;

import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.infomgt.data.security.Authority;
import org.iotp.server.exception.IoTPException;
import org.iotp.server.msghub.ThingsMetaKafkaTopics;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

@RestController
@RequestMapping("/api")
public class PluginController extends BaseController {

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.GET)
    @ResponseBody
    public PluginMetaData getPluginById(@PathVariable("pluginId") String strPluginId) throws IoTPException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            return checkPlugin(pluginService.findPluginById(pluginId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin/token/{pluginToken}", method = RequestMethod.GET)
    @ResponseBody
    public PluginMetaData getPluginByToken(@PathVariable("pluginToken") String pluginToken) throws IoTPException {
        checkParameter("pluginToken", pluginToken);
        try {
            return checkPlugin(pluginService.findPluginByApiToken(pluginToken));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin", method = RequestMethod.POST)
    @ResponseBody
    public PluginMetaData savePlugin(@RequestBody PluginMetaData source) throws IoTPException {
        try {
            boolean created = source.getId() == null;
            source.setTenantId(getCurrentUser().getTenantId());
            PluginMetaData plugin = checkNotNull(pluginService.savePlugin(source));
//            actorService.onPluginStateChange(plugin.getTenantId(), plugin.getId(),
//                    created ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, plugin.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.PLUGIN_ID, plugin.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, created ? ComponentLifecycleEvent.CREATED.name() : ComponentLifecycleEvent.UPDATED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_PLUGIN_TOPIC, plugin.getId().toString(), json.toString());
            return plugin;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin/{pluginId}/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void activatePluginById(@PathVariable("pluginId") String strPluginId) throws IoTPException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            PluginMetaData plugin = checkPlugin(pluginService.findPluginById(pluginId));
            pluginService.activatePluginById(pluginId);
            //actorService.onPluginStateChange(plugin.getTenantId(), plugin.getId(), ComponentLifecycleEvent.ACTIVATED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, plugin.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.PLUGIN_ID, plugin.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, ComponentLifecycleEvent.ACTIVATED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_PLUGIN_TOPIC, plugin.getId().toString(), json.toString());
          
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin/{pluginId}/suspend", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void suspendPluginById(@PathVariable("pluginId") String strPluginId) throws IoTPException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            PluginMetaData plugin = checkPlugin(pluginService.findPluginById(pluginId));
            pluginService.suspendPluginById(pluginId);
            //actorService.onPluginStateChange(plugin.getTenantId(), plugin.getId(), ComponentLifecycleEvent.SUSPENDED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, plugin.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.PLUGIN_ID, plugin.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, ComponentLifecycleEvent.SUSPENDED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_PLUGIN_TOPIC, plugin.getId().toString(), json.toString());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/plugin/system", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<PluginMetaData> getSystemPlugins(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        try {
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(pluginService.findSystemPlugins(pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/plugin/tenant/{tenantId}", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<PluginMetaData> getTenantPlugins(
            @PathVariable("tenantId") String strTenantId,
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        checkParameter("tenantId", strTenantId);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(pluginService.findTenantPlugins(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugins", method = RequestMethod.GET)
    @ResponseBody
    public List<PluginMetaData> getPlugins() throws IoTPException {
        try {
            if (getCurrentUser().getAuthority() == Authority.SYS_ADMIN) {
                return checkNotNull(pluginService.findSystemPlugins());
            } else {
                TenantId tenantId = getCurrentUser().getTenantId();
                List<PluginMetaData> plugins = checkNotNull(pluginService.findAllTenantPluginsByTenantId(tenantId));
                plugins.stream()
                        .filter(plugin -> plugin.getTenantId().getId().equals(ModelConstants.NULL_UUID))
                        .forEach(plugin -> plugin.setConfiguration(null));
                return plugins;
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/plugin", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<PluginMetaData> getTenantPlugins(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(pluginService.findTenantPlugins(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePlugin(@PathVariable("pluginId") String strPluginId) throws IoTPException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            PluginMetaData plugin = checkPlugin(pluginService.findPluginById(pluginId));
            pluginService.deletePluginById(pluginId);
            //actorService.onPluginStateChange(plugin.getTenantId(), plugin.getId(), ComponentLifecycleEvent.DELETED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, plugin.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.PLUGIN_ID, plugin.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, ComponentLifecycleEvent.DELETED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_PLUGIN_TOPIC, plugin.getId().toString(), json.toString());
        } catch (Exception e) {
            throw handleException(e);
        }
    }


}

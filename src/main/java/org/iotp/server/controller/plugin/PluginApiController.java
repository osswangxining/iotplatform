package org.iotp.server.controller.plugin;


import javax.servlet.http.HttpServletRequest;

import org.iotp.analytics.ruleengine.api.plugins.PluginApiCallSecurityContext;
import org.iotp.analytics.ruleengine.api.plugins.PluginConstants;
import org.iotp.analytics.ruleengine.plugins.rest.BasicPluginRestMsg;
import org.iotp.analytics.ruleengine.plugins.rest.RestRequest;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.dao.plugin.PluginService;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.server.actors.service.ActorService;
import org.iotp.server.controller.BaseController;
import org.iotp.server.exception.IoTPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(PluginConstants.PLUGIN_URL_PREFIX)
@Slf4j
public class PluginApiController extends BaseController {

    @Autowired
    private ActorService actorService;

    @Autowired
    private PluginService pluginService;

    @SuppressWarnings("rawtypes")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{pluginToken}/**")
    @ResponseStatus(value = HttpStatus.OK)
    public DeferredResult<ResponseEntity> processRequest(
            @PathVariable("pluginToken") String pluginToken,
            RequestEntity<byte[]> requestEntity,
            HttpServletRequest request)
            throws IoTPException {
        log.debug("[{}] Going to process requst uri: {}", pluginToken, requestEntity.getUrl());
        DeferredResult<ResponseEntity> result = new DeferredResult<ResponseEntity>();
        PluginMetaData pluginMd = pluginService.findPluginByApiToken(pluginToken);
        if (pluginMd == null) {
            result.setErrorResult(new PluginNotFoundException("Plugin with token: " + pluginToken + " not found!"));
        } else {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = getCurrentUser().getCustomerId();
            if (validatePluginAccess(pluginMd, tenantId, customerId)) {
                if(ModelConstants.NULL_UUID.equals(tenantId.getId())){
                    tenantId = null;
                }
                PluginApiCallSecurityContext securityCtx = new PluginApiCallSecurityContext(pluginMd.getTenantId(), pluginMd.getId(), tenantId, customerId);
                actorService.process(new BasicPluginRestMsg(securityCtx, new RestRequest(requestEntity, request), result));
            } else {
                result.setResult(new ResponseEntity<>(HttpStatus.FORBIDDEN));
            }

        }
        return result;
    }

    public static boolean validatePluginAccess(PluginMetaData pluginMd, TenantId tenantId, CustomerId customerId) {
        boolean systemAdministrator = tenantId == null || ModelConstants.NULL_UUID.equals(tenantId.getId());
        boolean tenantAdministrator = !systemAdministrator && (customerId == null || ModelConstants.NULL_UUID.equals(customerId.getId()));
        boolean systemPlugin = ModelConstants.NULL_UUID.equals(pluginMd.getTenantId().getId());

        boolean validUser = false;
        if (systemPlugin) {
            if (pluginMd.isPublicAccess() || systemAdministrator) {
                // All users can access public system plugins. Only system
                // users can access private system plugins
                validUser = true;
            }
        } else {
            if ((pluginMd.isPublicAccess() || tenantAdministrator) && tenantId.equals(pluginMd.getTenantId())) {
                // All tenant users can access public tenant plugins. Only tenant
                // administrator can access private tenant plugins
                validUser = true;
            }
        }
        return validUser;
    }
}

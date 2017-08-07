package org.iotp.server.controller;

import java.util.List;

import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.plugin.ComponentLifecycleEvent;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.infomgt.data.rule.RuleMetaData;
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
public class RuleController extends BaseController {

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}", method = RequestMethod.GET)
    @ResponseBody
    public RuleMetaData getRuleById(@PathVariable("ruleId") String strRuleId) throws IoTPException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            return checkRule(ruleService.findRuleById(ruleId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/token/{pluginToken}", method = RequestMethod.GET)
    @ResponseBody
    public List<RuleMetaData> getRulesByPluginToken(@PathVariable("pluginToken") String pluginToken) throws IoTPException {
        checkParameter("pluginToken", pluginToken);
        try {
            PluginMetaData plugin = checkPlugin(pluginService.findPluginByApiToken(pluginToken));
            return ruleService.findPluginRules(plugin.getApiToken());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule", method = RequestMethod.POST)
    @ResponseBody
    public RuleMetaData saveRule(@RequestBody RuleMetaData source) throws IoTPException {
        try {
            boolean created = source.getId() == null;
            source.setTenantId(getCurrentUser().getTenantId());
            RuleMetaData rule = checkNotNull(ruleService.saveRule(source));
//            actorService.onRuleStateChange(rule.getTenantId(), rule.getId(),
//                    created ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, rule.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.RULE_ID, rule.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, created ? ComponentLifecycleEvent.CREATED.name() : ComponentLifecycleEvent.UPDATED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_RULE_TOPIC, rule.getId().toString(), json.toString());
         
            return rule;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void activateRuleById(@PathVariable("ruleId") String strRuleId) throws IoTPException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            RuleMetaData rule = checkRule(ruleService.findRuleById(ruleId));
            ruleService.activateRuleById(ruleId);
            //actorService.onRuleStateChange(rule.getTenantId(), rule.getId(), ComponentLifecycleEvent.ACTIVATED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, rule.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.RULE_ID, rule.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, ComponentLifecycleEvent.ACTIVATED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_RULE_TOPIC, rule.getId().toString(), json.toString());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}/suspend", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void suspendRuleById(@PathVariable("ruleId") String strRuleId) throws IoTPException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            RuleMetaData rule = checkRule(ruleService.findRuleById(ruleId));
            ruleService.suspendRuleById(ruleId);
            //actorService.onRuleStateChange(rule.getTenantId(), rule.getId(), ComponentLifecycleEvent.SUSPENDED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, rule.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.RULE_ID, rule.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, ComponentLifecycleEvent.SUSPENDED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_RULE_TOPIC, rule.getId().toString(), json.toString());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/rule/system", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<RuleMetaData> getSystemRules(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        try {
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(ruleService.findSystemRules(pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/rule/tenant/{tenantId}", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<RuleMetaData> getTenantRules(
            @PathVariable("tenantId") String strTenantId,
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        checkParameter("tenantId", strTenantId);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(ruleService.findTenantRules(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    @ResponseBody
    public List<RuleMetaData> getRules() throws IoTPException {
        try {
            if (getCurrentUser().getAuthority() == Authority.SYS_ADMIN) {
                return checkNotNull(ruleService.findSystemRules());
            } else {
                TenantId tenantId = getCurrentUser().getTenantId();
                return checkNotNull(ruleService.findAllTenantRulesByTenantId(tenantId));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/rule", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<RuleMetaData> getTenantRules(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(ruleService.findTenantRules(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteRule(@PathVariable("ruleId") String strRuleId) throws IoTPException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            RuleMetaData rule = checkRule(ruleService.findRuleById(ruleId));
            ruleService.deleteRuleById(ruleId);
            //actorService.onRuleStateChange(rule.getTenantId(), rule.getId(), ComponentLifecycleEvent.DELETED);
            JsonObject json = new JsonObject();
            json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, rule.getTenantId().toString());
            json.addProperty(ThingsMetaKafkaTopics.RULE_ID, rule.getId().toString());
            json.addProperty(ThingsMetaKafkaTopics.EVENT, ComponentLifecycleEvent.DELETED.name());
            msgProducer.send(ThingsMetaKafkaTopics.METADATA_RULE_TOPIC, rule.getId().toString(), json.toString());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}

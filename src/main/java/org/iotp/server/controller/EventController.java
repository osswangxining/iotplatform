package org.iotp.server.controller;

import org.iotp.infomgt.dao.event.EventService;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.id.EntityIdFactory;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TimePageData;
import org.iotp.infomgt.data.page.TimePageLink;
import org.iotp.server.exception.IoTPErrorCode;
import org.iotp.server.exception.IoTPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventController extends BaseController {

    @Autowired
    private EventService eventService;

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/events/{entityType}/{entityId}/{eventType}", method = RequestMethod.GET)
    @ResponseBody
    public TimePageData<Event> getEvents(
            @PathVariable("entityType") String strEntityType,
            @PathVariable("entityId") String strEntityId,
            @PathVariable("eventType") String eventType,
            @RequestParam("tenantId") String strTenantId,
            @RequestParam int limit,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false, defaultValue = "false") boolean ascOrder,
            @RequestParam(required = false) String offset
    ) throws IoTPException {
        checkParameter("EntityId", strEntityId);
        checkParameter("EntityType", strEntityType);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
            if (!tenantId.getId().equals(ModelConstants.NULL_UUID) &&
                    !tenantId.equals(getCurrentUser().getTenantId())) {
                throw new IoTPException("You don't have permission to perform this operation!",
                        IoTPErrorCode.PERMISSION_DENIED);
            }
            TimePageLink pageLink = createPageLink(limit, startTime, endTime, ascOrder, offset);
            return checkNotNull(eventService.findEvents(tenantId, EntityIdFactory.getByTypeAndId(strEntityType, strEntityId), eventType, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/events/{entityType}/{entityId}", method = RequestMethod.GET)
    @ResponseBody
    public TimePageData<Event> getEvents(
            @PathVariable("entityType") String strEntityType,
            @PathVariable("entityId") String strEntityId,
            @RequestParam("tenantId") String strTenantId,
            @RequestParam int limit,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false, defaultValue = "false") boolean ascOrder,
            @RequestParam(required = false) String offset
    ) throws IoTPException {
        checkParameter("EntityId", strEntityId);
        checkParameter("EntityType", strEntityType);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
            if (!tenantId.getId().equals(ModelConstants.NULL_UUID) &&
                    !tenantId.equals(getCurrentUser().getTenantId())) {
                throw new IoTPException("You don't have permission to perform this operation!",
                        IoTPErrorCode.PERMISSION_DENIED);
            }
            TimePageLink pageLink = createPageLink(limit, startTime, endTime, ascOrder, offset);
            return checkNotNull(eventService.findEvents(tenantId, EntityIdFactory.getByTypeAndId(strEntityType, strEntityId), pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}

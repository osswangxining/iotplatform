package org.iotp.server.controller;

import java.util.List;

import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.id.WidgetsBundleId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.security.Authority;
import org.iotp.infomgt.data.widget.WidgetsBundle;
import org.iotp.server.exception.IoTPException;
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

@RestController
@RequestMapping("/api")
public class WidgetsBundleController extends BaseController {

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/widgetsBundle/{widgetsBundleId}", method = RequestMethod.GET)
    @ResponseBody
    public WidgetsBundle getWidgetsBundleById(@PathVariable("widgetsBundleId") String strWidgetsBundleId) throws IoTPException {
        checkParameter("widgetsBundleId", strWidgetsBundleId);
        try {
            WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
            return checkWidgetsBundleId(widgetsBundleId, false);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/widgetsBundle", method = RequestMethod.POST)
    @ResponseBody
    public WidgetsBundle saveWidgetsBundle(@RequestBody WidgetsBundle widgetsBundle) throws IoTPException {
        try {
            if (getCurrentUser().getAuthority() == Authority.SYS_ADMIN) {
                widgetsBundle.setTenantId(new TenantId(ModelConstants.NULL_UUID));
            } else {
                widgetsBundle.setTenantId(getCurrentUser().getTenantId());
            }
            return checkNotNull(widgetsBundleService.saveWidgetsBundle(widgetsBundle));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/widgetsBundle/{widgetsBundleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteWidgetsBundle(@PathVariable("widgetsBundleId") String strWidgetsBundleId) throws IoTPException {
        checkParameter("widgetsBundleId", strWidgetsBundleId);
        try {
            WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
            checkWidgetsBundleId(widgetsBundleId, true);
            widgetsBundleService.deleteWidgetsBundle(widgetsBundleId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/widgetsBundles", params = { "limit" }, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<WidgetsBundle> getWidgetsBundles(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        try {
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (getCurrentUser().getAuthority() == Authority.SYS_ADMIN) {
                return checkNotNull(widgetsBundleService.findSystemWidgetsBundlesByPageLink(pageLink));
            } else {
                TenantId tenantId = getCurrentUser().getTenantId();
                return checkNotNull(widgetsBundleService.findAllTenantWidgetsBundlesByTenantIdAndPageLink(tenantId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/widgetsBundles", method = RequestMethod.GET)
    @ResponseBody
    public List<WidgetsBundle> getWidgetsBundles() throws IoTPException {
        try {
            if (getCurrentUser().getAuthority() == Authority.SYS_ADMIN) {
                return checkNotNull(widgetsBundleService.findSystemWidgetsBundles());
            } else {
                TenantId tenantId = getCurrentUser().getTenantId();
                return checkNotNull(widgetsBundleService.findAllTenantWidgetsBundlesByTenantId(tenantId));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}

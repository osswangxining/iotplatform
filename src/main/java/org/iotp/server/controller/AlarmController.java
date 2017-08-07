package org.iotp.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.alarm.AlarmInfo;
import org.iotp.infomgt.data.alarm.AlarmQuery;
import org.iotp.infomgt.data.alarm.AlarmSearchStatus;
import org.iotp.infomgt.data.alarm.AlarmSeverity;
import org.iotp.infomgt.data.alarm.AlarmStatus;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.EntityIdFactory;
import org.iotp.infomgt.data.page.TimePageData;
import org.iotp.infomgt.data.page.TimePageLink;
import org.iotp.server.exception.IoTPErrorCode;
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
public class AlarmController extends BaseController {

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm/{alarmId}", method = RequestMethod.GET)
    @ResponseBody
    public Alarm getAlarmById(@PathVariable("alarmId") String strAlarmId) throws IoTPException {
        checkParameter("alarmId", strAlarmId);
        try {
            AlarmId alarmId = new AlarmId(toUUID(strAlarmId));
            return checkAlarmId(alarmId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm/info/{alarmId}", method = RequestMethod.GET)
    @ResponseBody
    public AlarmInfo getAlarmInfoById(@PathVariable("alarmId") String strAlarmId) throws IoTPException {
        checkParameter("alarmId", strAlarmId);
        try {
            AlarmId alarmId = new AlarmId(toUUID(strAlarmId));
            return checkAlarmInfoId(alarmId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm", method = RequestMethod.POST)
    @ResponseBody
    public Alarm saveAlarm(@RequestBody Alarm alarm) throws IoTPException {
        try {
            alarm.setTenantId(getCurrentUser().getTenantId());
            return checkNotNull(alarmService.createOrUpdateAlarm(alarm));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm/{alarmId}/ack", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void ackAlarm(@PathVariable("alarmId") String strAlarmId) throws IoTPException {
        checkParameter("alarmId", strAlarmId);
        try {
            AlarmId alarmId = new AlarmId(toUUID(strAlarmId));
            checkAlarmId(alarmId);
            alarmService.ackAlarm(alarmId, System.currentTimeMillis()).get();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm/{alarmId}/clear", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void clearAlarm(@PathVariable("alarmId") String strAlarmId) throws IoTPException {
        checkParameter("alarmId", strAlarmId);
        try {
            AlarmId alarmId = new AlarmId(toUUID(strAlarmId));
            checkAlarmId(alarmId);
            alarmService.clearAlarm(alarmId, System.currentTimeMillis()).get();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm/{entityType}/{entityId}", method = RequestMethod.GET)
    @ResponseBody
    public TimePageData<AlarmInfo> getAlarms(
            @PathVariable("entityType") String strEntityType,
            @PathVariable("entityId") String strEntityId,
            @RequestParam(required = false) String searchStatus,
            @RequestParam(required = false) String status,
            @RequestParam int limit,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false, defaultValue = "false") boolean ascOrder,
            @RequestParam(required = false) String offset,
            @RequestParam(required = false) Boolean fetchOriginator
    ) throws IoTPException {
        checkParameter("EntityId", strEntityId);
        checkParameter("EntityType", strEntityType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strEntityType, strEntityId);
        AlarmSearchStatus alarmSearchStatus = StringUtils.isEmpty(searchStatus) ? null : AlarmSearchStatus.valueOf(searchStatus);
        AlarmStatus alarmStatus = StringUtils.isEmpty(status) ? null : AlarmStatus.valueOf(status);
        if (alarmSearchStatus != null && alarmStatus != null) {
            throw new IoTPException("Invalid alarms search query: Both parameters 'searchStatus' " +
                    "and 'status' can't be specified at the same time!", IoTPErrorCode.BAD_REQUEST_PARAMS);
        }
        checkEntityId(entityId);
        try {
            TimePageLink pageLink = createPageLink(limit, startTime, endTime, ascOrder, offset);
            return checkNotNull(alarmService.findAlarms(new AlarmQuery(entityId, pageLink, alarmSearchStatus, alarmStatus, fetchOriginator)).get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/alarm/highestSeverity/{entityType}/{entityId}", method = RequestMethod.GET)
    @ResponseBody
    public AlarmSeverity getHighestAlarmSeverity(
            @PathVariable("entityType") String strEntityType,
            @PathVariable("entityId") String strEntityId,
            @RequestParam(required = false) String searchStatus,
            @RequestParam(required = false) String status
    ) throws IoTPException {
        checkParameter("EntityId", strEntityId);
        checkParameter("EntityType", strEntityType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strEntityType, strEntityId);
        AlarmSearchStatus alarmSearchStatus = StringUtils.isEmpty(searchStatus) ? null : AlarmSearchStatus.valueOf(searchStatus);
        AlarmStatus alarmStatus = StringUtils.isEmpty(status) ? null : AlarmStatus.valueOf(status);
        if (alarmSearchStatus != null && alarmStatus != null) {
            throw new IoTPException("Invalid alarms search query: Both parameters 'searchStatus' " +
                    "and 'status' can't be specified at the same time!", IoTPErrorCode.BAD_REQUEST_PARAMS);
        }
        checkEntityId(entityId);
        try {
            return alarmService.findHighestAlarmSeverity(entityId, alarmSearchStatus, alarmStatus);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}

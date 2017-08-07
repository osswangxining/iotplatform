package org.iotp.server.actors.rule;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.iotp.analytics.ruleengine.api.device.DeviceMetaData;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.infomgt.dao.alarm.AlarmService;
import org.iotp.infomgt.dao.event.EventService;
import org.iotp.infomgt.dao.timeseries.TimeseriesService;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.server.actors.ActorSystemContext;

import com.google.common.util.concurrent.ListenableFuture;

public class RuleProcessingContext implements RuleContext {

  private final TimeseriesService tsService;
  private final EventService eventService;
  private final AlarmService alarmService;
  private final RuleId ruleId;
  private TenantId tenantId;
  private CustomerId customerId;
  private DeviceId deviceId;
  private DeviceMetaData deviceMetaData;

  RuleProcessingContext(ActorSystemContext systemContext, RuleId ruleId) {
    this.tsService = systemContext.getTsService();
    this.eventService = systemContext.getEventService();
    this.alarmService = systemContext.getAlarmService();
    this.ruleId = ruleId;
  }

  void update(ToDeviceActorMsg toDeviceActorMsg, DeviceMetaData deviceMetaData) {
    this.tenantId = toDeviceActorMsg.getTenantId();
    this.customerId = toDeviceActorMsg.getCustomerId();
    this.deviceId = toDeviceActorMsg.getDeviceId();
    this.deviceMetaData = deviceMetaData;
  }

  @Override
  public RuleId getRuleId() {
    return ruleId;
  }

  @Override
  public DeviceMetaData getDeviceMetaData() {
    return deviceMetaData;
  }

  @Override
  public Event save(Event event) {
    checkEvent(event);
    return eventService.save(event);
  }

  @Override
  public Optional<Event> saveIfNotExists(Event event) {
    checkEvent(event);
    return eventService.saveIfNotExists(event);
  }

  @Override
  public Optional<Event> findEvent(String eventType, String eventUid) {
    return eventService.findEvent(tenantId, deviceId, eventType, eventUid);
  }

  @Override
  public Alarm createOrUpdateAlarm(Alarm alarm) {
    alarm.setTenantId(tenantId);
    return alarmService.createOrUpdateAlarm(alarm);
  }

  public Optional<Alarm> findLatestAlarm(EntityId originator, String alarmType) {
    try {
      return Optional.ofNullable(alarmService.findLatestByOriginatorAndType(tenantId, originator, alarmType).get());
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Failed to lookup alarm!", e);
    }
  }

  @Override
  public ListenableFuture<Boolean> clearAlarm(AlarmId alarmId, long clearTs) {
    return alarmService.clearAlarm(alarmId, clearTs);
  }

  private void checkEvent(Event event) {
    if (event.getTenantId() == null) {
      event.setTenantId(tenantId);
    } else if (!tenantId.equals(event.getTenantId())) {
      throw new IllegalArgumentException("Invalid Tenant id!");
    }
    if (event.getEntityId() == null) {
      event.setEntityId(deviceId);
    }
  }
}

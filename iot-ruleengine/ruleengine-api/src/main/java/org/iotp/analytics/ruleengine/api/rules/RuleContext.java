package org.iotp.analytics.ruleengine.api.rules;

import java.util.Optional;

import org.iotp.analytics.ruleengine.api.device.DeviceMetaData;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.RuleId;

import com.google.common.util.concurrent.ListenableFuture;

public interface RuleContext {

  RuleId getRuleId();

  DeviceMetaData getDeviceMetaData();

  Event save(Event event);

  Optional<Event> saveIfNotExists(Event event);

  Optional<Event> findEvent(String eventType, String eventUid);

  Optional<Alarm> findLatestAlarm(EntityId originator, String alarmType);

  Alarm createOrUpdateAlarm(Alarm alarm);

  ListenableFuture<Boolean> clearAlarm(AlarmId id, long clearTs);
}

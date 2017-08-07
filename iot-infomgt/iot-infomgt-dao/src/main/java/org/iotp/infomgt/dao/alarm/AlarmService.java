/*******************************************************************************
 * Copyright 2017 osswangxining@163.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.iotp.infomgt.dao.alarm;

import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.alarm.AlarmInfo;
import org.iotp.infomgt.data.alarm.AlarmQuery;
import org.iotp.infomgt.data.alarm.AlarmSearchStatus;
import org.iotp.infomgt.data.alarm.AlarmSeverity;
import org.iotp.infomgt.data.alarm.AlarmStatus;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TimePageData;

import com.google.common.util.concurrent.ListenableFuture;

/**
 */
public interface AlarmService {

    Alarm createOrUpdateAlarm(Alarm alarm);

    ListenableFuture<Boolean> ackAlarm(AlarmId alarmId, long ackTs);

    ListenableFuture<Boolean> clearAlarm(AlarmId alarmId, long ackTs);

    ListenableFuture<Alarm> findAlarmByIdAsync(AlarmId alarmId);

    ListenableFuture<AlarmInfo> findAlarmInfoByIdAsync(AlarmId alarmId);

    ListenableFuture<TimePageData<AlarmInfo>> findAlarms(AlarmQuery query);

    AlarmSeverity findHighestAlarmSeverity(EntityId entityId, AlarmSearchStatus alarmSearchStatus,
                                           AlarmStatus alarmStatus);

    ListenableFuture<Alarm> findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type);

}

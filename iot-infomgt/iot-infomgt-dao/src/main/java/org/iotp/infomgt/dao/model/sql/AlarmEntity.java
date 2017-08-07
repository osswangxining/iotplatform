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
/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iotp.infomgt.dao.model.sql;

import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_ACK_TS_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_CLEAR_TS_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_END_TS_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_ORIGINATOR_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_ORIGINATOR_TYPE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_PROPAGATE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_SEVERITY_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_START_TS_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_STATUS_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_TENANT_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ALARM_TYPE_PROPERTY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.iotp.infomgt.dao.model.BaseEntity;
import org.iotp.infomgt.dao.model.BaseSqlEntity;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.dao.util.mapping.JsonStringType;
import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.alarm.AlarmSeverity;
import org.iotp.infomgt.data.alarm.AlarmStatus;
import org.iotp.infomgt.data.common.ThingType;
import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.EntityIdFactory;
import org.iotp.infomgt.data.id.TenantId;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ALARM_COLUMN_FAMILY_NAME)
public final class AlarmEntity extends BaseSqlEntity<Alarm> implements BaseEntity<Alarm> {

    @Transient
    private static final long serialVersionUID = -339979717281685984L;

    @Column(name = ALARM_TENANT_ID_PROPERTY)
    private String tenantId;

    @Column(name = ALARM_ORIGINATOR_ID_PROPERTY)
    private String originatorId;

    @Column(name = ALARM_ORIGINATOR_TYPE_PROPERTY)
    private ThingType originatorType;

    @Column(name = ALARM_TYPE_PROPERTY)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = ALARM_SEVERITY_PROPERTY)
    private AlarmSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = ALARM_STATUS_PROPERTY)
    private AlarmStatus status;

    @Column(name = ALARM_START_TS_PROPERTY)
    private Long startTs;

    @Column(name = ALARM_END_TS_PROPERTY)
    private Long endTs;

    @Column(name = ALARM_ACK_TS_PROPERTY)
    private Long ackTs;

    @Column(name = ALARM_CLEAR_TS_PROPERTY)
    private Long clearTs;

    @Type(type = "json")
    @Column(name = ModelConstants.ASSET_ADDITIONAL_INFO_PROPERTY)
    private JsonNode details;

    @Column(name = ALARM_PROPAGATE_PROPERTY)
    private Boolean propagate;

    public AlarmEntity() {
        super();
    }

    public AlarmEntity(Alarm alarm) {
        if (alarm.getId() != null) {
            this.setId(alarm.getId().getId());
        }
        if (alarm.getTenantId() != null) {
            this.tenantId = UUIDConverter.fromTimeUUID(alarm.getTenantId().getId());
        }
        this.type = alarm.getType();
        this.originatorId = UUIDConverter.fromTimeUUID(alarm.getOriginator().getId());
        this.originatorType = alarm.getOriginator().getEntityType();
        this.type = alarm.getType();
        this.severity = alarm.getSeverity();
        this.status = alarm.getStatus();
        this.propagate = alarm.isPropagate();
        this.startTs = alarm.getStartTs();
        this.endTs = alarm.getEndTs();
        this.ackTs = alarm.getAckTs();
        this.clearTs = alarm.getClearTs();
        this.details = alarm.getDetails();
    }

    @Override
    public Alarm toData() {
        Alarm alarm = new Alarm(new AlarmId(UUIDConverter.fromString(id)));
        alarm.setCreatedTime(UUIDs.unixTimestamp(UUIDConverter.fromString(id)));
        if (tenantId != null) {
            alarm.setTenantId(new TenantId(UUIDConverter.fromString(tenantId)));
        }
        alarm.setOriginator(EntityIdFactory.getByTypeAndUuid(originatorType, UUIDConverter.fromString(originatorId)));
        alarm.setType(type);
        alarm.setSeverity(severity);
        alarm.setStatus(status);
        alarm.setPropagate(propagate);
        alarm.setStartTs(startTs);
        alarm.setEndTs(endTs);
        alarm.setAckTs(ackTs);
        alarm.setClearTs(clearTs);
        alarm.setDetails(details);
        return alarm;
    }

}

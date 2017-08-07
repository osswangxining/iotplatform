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
package org.iotp.infomgt.dao.model.nosql;

import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_BODY_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_ENTITY_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_ENTITY_TYPE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_TENANT_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_TYPE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.EVENT_UID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ID_PROPERTY;

import java.util.UUID;

import org.iotp.infomgt.dao.model.BaseEntity;
import org.iotp.infomgt.dao.model.type.EntityTypeCodec;
import org.iotp.infomgt.dao.model.type.JsonCodec;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.common.ThingType;
import org.iotp.infomgt.data.id.EntityIdFactory;
import org.iotp.infomgt.data.id.EventId;
import org.iotp.infomgt.data.id.TenantId;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Andrew Shvayka
 */
@Data
@NoArgsConstructor
@Table(name = EVENT_COLUMN_FAMILY_NAME)
public class EventEntity implements BaseEntity<Event> {

    @Transient
    private static final long serialVersionUID = -1265181166886910153L;

    @Column(name = ID_PROPERTY)
    private UUID id;

    @PartitionKey()
    @Column(name = EVENT_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @PartitionKey(value = 1)
    @Column(name = EVENT_ENTITY_TYPE_PROPERTY, codec = EntityTypeCodec.class)
    private ThingType entityType;

    @PartitionKey(value = 2)
    @Column(name = EVENT_ENTITY_ID_PROPERTY)
    private UUID entityId;

    @ClusteringColumn()
    @Column(name = EVENT_TYPE_PROPERTY)
    private String eventType;

    @ClusteringColumn(value = 1)
    @Column(name = EVENT_UID_PROPERTY)
    private String eventUid;

    @Column(name = EVENT_BODY_PROPERTY, codec = JsonCodec.class)
    private JsonNode body;

    public EventEntity(Event event) {
        if (event.getId() != null) {
            this.id = event.getId().getId();
        }
        if (event.getTenantId() != null) {
            this.tenantId = event.getTenantId().getId();
        }
        if (event.getEntityId() != null) {
            this.entityType = event.getEntityId().getEntityType();
            this.entityId = event.getEntityId().getId();
        }
        this.eventType = event.getType();
        this.eventUid = event.getUid();
        this.body = event.getBody();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Event toData() {
        Event event = new Event(new EventId(id));
        event.setCreatedTime(UUIDs.unixTimestamp(id));
        event.setTenantId(new TenantId(tenantId));
        event.setEntityId(EntityIdFactory.getByTypeAndUuid(entityType, entityId));
        event.setBody(body);
        event.setType(eventType);
        event.setUid(eventUid);
        return event;
    }
}

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
package org.iotp.infomgt.dao.event;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.iotp.infomgt.dao.exception.DataValidationException;
import org.iotp.infomgt.dao.util.DataValidator;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TimePageData;
import org.iotp.infomgt.data.page.TimePageLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseEventService implements EventService {

    @Autowired
    public EventDao eventDao;

    @Override
    public Event save(Event event) {
        eventValidator.validate(event);
        return eventDao.save(event);
    }

    @Override
    public Optional<Event> saveIfNotExists(Event event) {
        eventValidator.validate(event);
        if (StringUtils.isEmpty(event.getUid())) {
            throw new DataValidationException("Event uid should be specified!.");
        }
        return eventDao.saveIfNotExists(event);
    }

    @Override
    public Optional<Event> findEvent(TenantId tenantId, EntityId entityId, String eventType, String eventUid) {
        if (tenantId == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        if (entityId == null) {
            throw new DataValidationException("Entity id should be specified!.");
        }
        if (StringUtils.isEmpty(eventType)) {
            throw new DataValidationException("Event type should be specified!.");
        }
        if (StringUtils.isEmpty(eventUid)) {
            throw new DataValidationException("Event uid should be specified!.");
        }
        Event event = eventDao.findEvent(tenantId.getId(), entityId, eventType, eventUid);
        return event != null ? Optional.of(event) : Optional.empty();
    }

    @Override
    public TimePageData<Event> findEvents(TenantId tenantId, EntityId entityId, TimePageLink pageLink) {
        List<Event> events = eventDao.findEvents(tenantId.getId(), entityId, pageLink);
        return new TimePageData<>(events, pageLink);
    }

    @Override
    public TimePageData<Event> findEvents(TenantId tenantId, EntityId entityId, String eventType, TimePageLink pageLink) {
        List<Event> events = eventDao.findEvents(tenantId.getId(), entityId, eventType, pageLink);
        return new TimePageData<>(events, pageLink);
    }

    private DataValidator<Event> eventValidator =
            new DataValidator<Event>() {
                @Override
                protected void validateDataImpl(Event event) {
                    if (event.getEntityId() == null) {
                        throw new DataValidationException("Entity id should be specified!.");
                    }
                    if (StringUtils.isEmpty(event.getType())) {
                        throw new DataValidationException("Event type should be specified!.");
                    }
                    if (event.getBody() == null) {
                        throw new DataValidationException("Event body should be specified!.");
                    }
                }
            };
}

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

import java.util.Optional;

import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TimePageData;
import org.iotp.infomgt.data.page.TimePageLink;

public interface EventService {

    Event save(Event event);

    Optional<Event> saveIfNotExists(Event event);

    Optional<Event> findEvent(TenantId tenantId, EntityId entityId, String eventType, String eventUid);

    TimePageData<Event> findEvents(TenantId tenantId, EntityId entityId, TimePageLink pageLink);

    TimePageData<Event> findEvents(TenantId tenantId, EntityId entityId, String eventType, TimePageLink pageLink);
}

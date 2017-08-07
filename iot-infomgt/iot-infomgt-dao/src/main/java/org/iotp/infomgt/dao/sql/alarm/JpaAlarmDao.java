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
package org.iotp.infomgt.dao.sql.alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.alarm.AlarmDao;
import org.iotp.infomgt.dao.alarm.BaseAlarmService;
import org.iotp.infomgt.dao.model.sql.AlarmEntity;
import org.iotp.infomgt.dao.relation.RelationDao;
import org.iotp.infomgt.dao.sql.JpaAbstractDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.alarm.AlarmInfo;
import org.iotp.infomgt.data.alarm.AlarmQuery;
import org.iotp.infomgt.data.alarm.AlarmSearchStatus;
import org.iotp.infomgt.data.common.ThingType;
import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.relation.EntityRelation;
import org.iotp.infomgt.data.relation.RelationTypeGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Valerii Sosliuk on 5/19/2017.
 */
@Slf4j
@Component
@SqlDao
public class JpaAlarmDao extends JpaAbstractDao<AlarmEntity, Alarm> implements AlarmDao {

  @Autowired
  private AlarmRepository alarmRepository;

  @Autowired
  private RelationDao relationDao;

  @Override
  protected Class<AlarmEntity> getEntityClass() {
    return AlarmEntity.class;
  }

  @Override
  protected CrudRepository<AlarmEntity, String> getCrudRepository() {
    return alarmRepository;
  }

  @Override
  public ListenableFuture<Alarm> findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type) {
    return service.submit(() -> {
      List<AlarmEntity> latest = alarmRepository.findLatestByOriginatorAndType(
          UUIDConverter.fromTimeUUID(tenantId.getId()), UUIDConverter.fromTimeUUID(originator.getId()),
          originator.getEntityType(), type, new PageRequest(0, 1));
      return latest.isEmpty() ? null : DaoUtil.getData(latest.get(0));
    });
  }

  @Override
  public ListenableFuture<Alarm> findAlarmByIdAsync(UUID key) {
    return findByIdAsync(key);
  }

  @Override
  public ListenableFuture<List<AlarmInfo>> findAlarms(AlarmQuery query) {
    log.trace("Try to find alarms by entity [{}], status [{}] and pageLink [{}]", query.getAffectedEntityId(),
        query.getStatus(), query.getPageLink());
    EntityId affectedEntity = query.getAffectedEntityId();
    String searchStatusName;
    if (query.getSearchStatus() == null && query.getStatus() == null) {
      searchStatusName = AlarmSearchStatus.ANY.name();
    } else if (query.getSearchStatus() != null) {
      searchStatusName = query.getSearchStatus().name();
    } else {
      searchStatusName = query.getStatus().name();
    }
    String relationType = BaseAlarmService.ALARM_RELATION_PREFIX + searchStatusName;
    ListenableFuture<List<EntityRelation>> relations = relationDao.findRelations(affectedEntity, relationType,
        RelationTypeGroup.ALARM, ThingType.ALARM, query.getPageLink());
    return Futures.transform(relations, (AsyncFunction<List<EntityRelation>, List<AlarmInfo>>) input -> {
      List<ListenableFuture<AlarmInfo>> alarmFutures = new ArrayList<>(input.size());
      for (EntityRelation relation : input) {
        alarmFutures.add(Futures.transform(findAlarmByIdAsync(relation.getTo().getId()),
            (Function<Alarm, AlarmInfo>) AlarmInfo::new));
      }
      return Futures.successfulAsList(alarmFutures);
    });
  }
}

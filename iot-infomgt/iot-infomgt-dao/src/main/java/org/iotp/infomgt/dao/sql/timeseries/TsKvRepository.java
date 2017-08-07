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
package org.iotp.infomgt.dao.sql.timeseries;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.iotp.infomgt.dao.model.sql.TsKvCompositeKey;
import org.iotp.infomgt.dao.model.sql.TsKvEntity;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.common.ThingType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

@SqlDao
public interface TsKvRepository extends CrudRepository<TsKvEntity, TsKvCompositeKey> {

  @Query("SELECT tskv FROM TsKvEntity tskv WHERE tskv.entityId = :entityId "
      + "AND tskv.entityType = :entityType AND tskv.key = :entityKey "
      + "AND tskv.ts > :startTs AND tskv.ts < :endTs ORDER BY tskv.ts DESC")
  List<TsKvEntity> findAllWithLimit(@Param("entityId") String entityId, @Param("entityType") ThingType entityType,
      @Param("entityKey") String key, @Param("startTs") long startTs, @Param("endTs") long endTs, Pageable pageable);

  @Async
  @Query("SELECT new TsKvEntity(MAX(tskv.strValue), MAX(tskv.longValue), MAX(tskv.doubleValue)) FROM TsKvEntity tskv "
      + "WHERE tskv.entityId = :entityId AND tskv.entityType = :entityType "
      + "AND tskv.key = :entityKey AND tskv.ts > :startTs AND tskv.ts < :endTs")
  CompletableFuture<TsKvEntity> findMax(@Param("entityId") String entityId, @Param("entityType") ThingType entityType,
      @Param("entityKey") String entityKey, @Param("startTs") long startTs, @Param("endTs") long endTs);

  @Async
  @Query("SELECT new TsKvEntity(MIN(tskv.strValue), MIN(tskv.longValue), MIN(tskv.doubleValue)) FROM TsKvEntity tskv "
      + "WHERE tskv.entityId = :entityId AND tskv.entityType = :entityType "
      + "AND tskv.key = :entityKey AND tskv.ts > :startTs AND tskv.ts < :endTs")
  CompletableFuture<TsKvEntity> findMin(@Param("entityId") String entityId, @Param("entityType") ThingType entityType,
      @Param("entityKey") String entityKey, @Param("startTs") long startTs, @Param("endTs") long endTs);

  @Async
  @Query("SELECT new TsKvEntity(COUNT(tskv.booleanValue), COUNT(tskv.strValue), COUNT(tskv.longValue), COUNT(tskv.doubleValue)) FROM TsKvEntity tskv "
      + "WHERE tskv.entityId = :entityId AND tskv.entityType = :entityType "
      + "AND tskv.key = :entityKey AND tskv.ts > :startTs AND tskv.ts < :endTs")
  CompletableFuture<TsKvEntity> findCount(@Param("entityId") String entityId, @Param("entityType") ThingType entityType,
      @Param("entityKey") String entityKey, @Param("startTs") long startTs, @Param("endTs") long endTs);

  @Async
  @Query("SELECT new TsKvEntity(AVG(tskv.longValue), AVG(tskv.doubleValue)) FROM TsKvEntity tskv "
      + "WHERE tskv.entityId = :entityId AND tskv.entityType = :entityType "
      + "AND tskv.key = :entityKey AND tskv.ts > :startTs AND tskv.ts < :endTs")
  CompletableFuture<TsKvEntity> findAvg(@Param("entityId") String entityId, @Param("entityType") ThingType entityType,
      @Param("entityKey") String entityKey, @Param("startTs") long startTs, @Param("endTs") long endTs);

  @Async
  @Query("SELECT new TsKvEntity(SUM(tskv.longValue), SUM(tskv.doubleValue)) FROM TsKvEntity tskv "
      + "WHERE tskv.entityId = :entityId AND tskv.entityType = :entityType "
      + "AND tskv.key = :entityKey AND tskv.ts > :startTs AND tskv.ts < :endTs")
  CompletableFuture<TsKvEntity> findSum(@Param("entityId") String entityId, @Param("entityType") ThingType entityType,
      @Param("entityKey") String entityKey, @Param("startTs") long startTs, @Param("endTs") long endTs);
}

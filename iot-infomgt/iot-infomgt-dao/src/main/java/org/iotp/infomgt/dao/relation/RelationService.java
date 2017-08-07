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
package org.iotp.infomgt.dao.relation;

import java.util.List;

import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.relation.EntityRelation;
import org.iotp.infomgt.data.relation.EntityRelationInfo;
import org.iotp.infomgt.data.relation.RelationTypeGroup;

import com.google.common.util.concurrent.ListenableFuture;

/**
 */
public interface RelationService {

    ListenableFuture<Boolean> checkRelation(EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<EntityRelation> getRelation(EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<Boolean> saveRelation(EntityRelation relation);

    ListenableFuture<Boolean> deleteRelation(EntityRelation relation);

    ListenableFuture<Boolean> deleteRelation(EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<Boolean> deleteEntityRelations(EntityId entity);

    ListenableFuture<List<EntityRelation>> findByFrom(EntityId from, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelationInfo>> findInfoByFrom(EntityId from, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByFromAndType(EntityId from, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByTo(EntityId to, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelationInfo>> findInfoByTo(EntityId to, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByToAndType(EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByQuery(EntityRelationsQuery query);

    ListenableFuture<List<EntityRelationInfo>> findInfoByQuery(EntityRelationsQuery query);

//    TODO: This method may be useful for some validations in the future
//    ListenableFuture<Boolean> checkRecursiveRelation(EntityId from, EntityId to);

}

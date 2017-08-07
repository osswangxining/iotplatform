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
package org.iotp.infomgt.dao.sql.relation;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.sql.RelationCompositeKey;
import org.iotp.infomgt.dao.model.sql.RelationEntity;
import org.iotp.infomgt.dao.relation.RelationDao;
import org.iotp.infomgt.dao.sql.JpaAbstractDaoListeningExecutorService;
import org.iotp.infomgt.dao.sql.JpaAbstractSearchTimeDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.common.ThingType;
import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.page.TimePageLink;
import org.iotp.infomgt.data.relation.EntityRelation;
import org.iotp.infomgt.data.relation.RelationTypeGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Valerii Sosliuk on 5/29/2017.
 */
@Slf4j
@Component
@SqlDao
public class JpaRelationDao extends JpaAbstractDaoListeningExecutorService implements RelationDao {

  @Autowired
  private RelationRepository relationRepository;

  @Override
  public ListenableFuture<List<EntityRelation>> findAllByFrom(EntityId from, RelationTypeGroup typeGroup) {
    return service
        .submit(() -> DaoUtil.convertDataList(relationRepository.findAllByFromIdAndFromTypeAndRelationTypeGroup(
            UUIDConverter.fromTimeUUID(from.getId()), from.getEntityType().name(), typeGroup.name())));
  }

  @Override
  public ListenableFuture<List<EntityRelation>> findAllByFromAndType(EntityId from, String relationType,
      RelationTypeGroup typeGroup) {
    return service.submit(
        () -> DaoUtil.convertDataList(relationRepository.findAllByFromIdAndFromTypeAndRelationTypeAndRelationTypeGroup(
            UUIDConverter.fromTimeUUID(from.getId()), from.getEntityType().name(), relationType, typeGroup.name())));
  }

  @Override
  public ListenableFuture<List<EntityRelation>> findAllByTo(EntityId to, RelationTypeGroup typeGroup) {
    return service.submit(() -> DaoUtil.convertDataList(relationRepository.findAllByToIdAndToTypeAndRelationTypeGroup(
        UUIDConverter.fromTimeUUID(to.getId()), to.getEntityType().name(), typeGroup.name())));
  }

  @Override
  public ListenableFuture<List<EntityRelation>> findAllByToAndType(EntityId to, String relationType,
      RelationTypeGroup typeGroup) {
    return service.submit(
        () -> DaoUtil.convertDataList(relationRepository.findAllByToIdAndToTypeAndRelationTypeAndRelationTypeGroup(
            UUIDConverter.fromTimeUUID(to.getId()), to.getEntityType().name(), relationType, typeGroup.name())));
  }

  @Override
  public ListenableFuture<Boolean> checkRelation(EntityId from, EntityId to, String relationType,
      RelationTypeGroup typeGroup) {
    RelationCompositeKey key = getRelationCompositeKey(from, to, relationType, typeGroup);
    return service.submit(() -> relationRepository.findOne(key) != null);
  }

  @Override
  public ListenableFuture<EntityRelation> getRelation(EntityId from, EntityId to, String relationType,
      RelationTypeGroup typeGroup) {
    RelationCompositeKey key = getRelationCompositeKey(from, to, relationType, typeGroup);
    return service.submit(() -> DaoUtil.getData(relationRepository.findOne(key)));
  }

  private RelationCompositeKey getRelationCompositeKey(EntityId from, EntityId to, String relationType,
      RelationTypeGroup typeGroup) {
    return new RelationCompositeKey(UUIDConverter.fromTimeUUID(from.getId()), from.getEntityType().name(),
        UUIDConverter.fromTimeUUID(to.getId()), to.getEntityType().name(), relationType, typeGroup.name());
  }

  @Override
  public ListenableFuture<Boolean> saveRelation(EntityRelation relation) {
    return service.submit(() -> relationRepository.save(new RelationEntity(relation)) != null);
  }

  @Override
  public ListenableFuture<Boolean> deleteRelation(EntityRelation relation) {
    RelationCompositeKey key = new RelationCompositeKey(relation);
    return service.submit(() -> {
      boolean relationExistsBeforeDelete = relationRepository.exists(key);
      relationRepository.delete(key);
      return relationExistsBeforeDelete;
    });
  }

  @Override
  public ListenableFuture<Boolean> deleteRelation(EntityId from, EntityId to, String relationType,
      RelationTypeGroup typeGroup) {
    RelationCompositeKey key = getRelationCompositeKey(from, to, relationType, typeGroup);
    return service.submit(() -> {
      boolean relationExistsBeforeDelete = relationRepository.exists(key);
      relationRepository.delete(key);
      return relationExistsBeforeDelete;
    });
  }

  @Override
  public ListenableFuture<Boolean> deleteOutboundRelations(EntityId entity) {
    return service.submit(() -> {
      boolean relationExistsBeforeDelete = relationRepository
          .findAllByFromIdAndFromType(UUIDConverter.fromTimeUUID(entity.getId()), entity.getEntityType().name())
          .size() > 0;
      relationRepository.deleteByFromIdAndFromType(UUIDConverter.fromTimeUUID(entity.getId()),
          entity.getEntityType().name());
      return relationExistsBeforeDelete;
    });
  }

  @Override
  public ListenableFuture<List<EntityRelation>> findRelations(EntityId from, String relationType,
      RelationTypeGroup typeGroup, ThingType childType, TimePageLink pageLink) {
    Specification<RelationEntity> timeSearchSpec = JpaAbstractSearchTimeDao
        .<RelationEntity>getTimeSearchPageSpec(pageLink, "toId");
    Specification<RelationEntity> fieldsSpec = getEntityFieldsSpec(from, relationType, typeGroup, childType);
    Pageable pageable = new PageRequest(0, pageLink.getLimit(),
        new Sort(new Order(ASC, "relationTypeGroup"), new Order(ASC, "relationType"), new Order(ASC, "toType")));
    return service.submit(() -> DaoUtil
        .convertDataList(relationRepository.findAll(where(timeSearchSpec).and(fieldsSpec), pageable).getContent()));
  }

  private Specification<RelationEntity> getEntityFieldsSpec(EntityId from, String relationType,
      RelationTypeGroup typeGroup, ThingType childType) {
    return new Specification<RelationEntity>() {
      @Override
      public Predicate toPredicate(Root<RelationEntity> root, CriteriaQuery<?> criteriaQuery,
          CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (from != null) {
          Predicate fromIdPredicate = criteriaBuilder.equal(root.get("fromId"),
              UUIDConverter.fromTimeUUID(from.getId()));
          predicates.add(fromIdPredicate);
          Predicate fromEntityTypePredicate = criteriaBuilder.equal(root.get("fromType"), from.getEntityType().name());
          predicates.add(fromEntityTypePredicate);
        }
        if (relationType != null) {
          Predicate relationTypePredicate = criteriaBuilder.equal(root.get("relationType"), relationType);
          predicates.add(relationTypePredicate);
        }
        if (typeGroup != null) {
          Predicate typeGroupPredicate = criteriaBuilder.equal(root.get("relationTypeGroup"), typeGroup.name());
          predicates.add(typeGroupPredicate);
        }
        if (childType != null) {
          Predicate childTypePredicate = criteriaBuilder.equal(root.get("toType"), childType.name());
          predicates.add(childTypePredicate);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
      }
    };
  }
}

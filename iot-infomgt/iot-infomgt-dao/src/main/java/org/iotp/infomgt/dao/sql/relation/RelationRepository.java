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

import org.iotp.infomgt.dao.model.sql.RelationCompositeKey;
import org.iotp.infomgt.dao.model.sql.RelationEntity;
import org.iotp.infomgt.dao.util.SqlDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SqlDao
public interface RelationRepository
        extends CrudRepository<RelationEntity, RelationCompositeKey>, JpaSpecificationExecutor<RelationEntity> {

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeGroup(String fromId,
                                                                        String fromType,
                                                                        String relationTypeGroup);

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeAndRelationTypeGroup(String fromId,
                                                                                       String fromType,
                                                                                       String relationType,
                                                                                       String relationTypeGroup);

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeGroup(String toId,
                                                                    String toType,
                                                                    String relationTypeGroup);

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeAndRelationTypeGroup(String toId,
                                                                                   String toType,
                                                                                   String relationType,
                                                                                   String relationTypeGroup);

    List<RelationEntity> findAllByFromIdAndFromType(String fromId,
                                                    String fromType);

    @Transactional
    void deleteByFromIdAndFromType(String fromId, String fromType);
}

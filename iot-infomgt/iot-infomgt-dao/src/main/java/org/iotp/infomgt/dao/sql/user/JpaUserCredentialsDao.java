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
package org.iotp.infomgt.dao.sql.user;

import java.util.UUID;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.sql.UserCredentialsEntity;
import org.iotp.infomgt.dao.sql.JpaAbstractDao;
import org.iotp.infomgt.dao.user.UserCredentialsDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.security.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Valerii Sosliuk on 4/22/2017.
 */
@Component
@SqlDao
public class JpaUserCredentialsDao extends JpaAbstractDao<UserCredentialsEntity, UserCredentials> implements UserCredentialsDao {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Override
    protected Class<UserCredentialsEntity> getEntityClass() {
        return UserCredentialsEntity.class;
    }

    @Override
    protected CrudRepository<UserCredentialsEntity, String> getCrudRepository() {
        return userCredentialsRepository;
    }

    @Override
    public UserCredentials findByUserId(UUID userId) {
        return DaoUtil.getData(userCredentialsRepository.findByUserId(UUIDConverter.fromTimeUUID(userId)));
    }

    @Override
    public UserCredentials findByActivateToken(String activateToken) {
        return DaoUtil.getData(userCredentialsRepository.findByActivateToken(activateToken));
    }

    @Override
    public UserCredentials findByResetToken(String resetToken) {
        return DaoUtil.getData(userCredentialsRepository.findByResetToken(resetToken));
    }
}

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
package org.iotp.infomgt.dao.sql.settings;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.sql.AdminSettingsEntity;
import org.iotp.infomgt.dao.settings.AdminSettingsDao;
import org.iotp.infomgt.dao.sql.JpaAbstractDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.AdminSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@SqlDao
public class JpaAdminSettingsDao extends JpaAbstractDao<AdminSettingsEntity, AdminSettings> implements AdminSettingsDao {

    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @Override
    protected Class<AdminSettingsEntity> getEntityClass() {
        return AdminSettingsEntity.class;
    }

    @Override
    protected CrudRepository<AdminSettingsEntity, String> getCrudRepository() {
        return adminSettingsRepository;
    }

    @Override
    public AdminSettings findByKey(String key) {
        return DaoUtil.getData(adminSettingsRepository.findByKey(key));
    }
}

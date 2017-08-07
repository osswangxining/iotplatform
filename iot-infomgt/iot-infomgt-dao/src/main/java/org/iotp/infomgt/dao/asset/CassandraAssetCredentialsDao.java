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
package org.iotp.infomgt.dao.asset;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

import java.util.UUID;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.dao.model.nosql.AssetCredentialsEntity;
import org.iotp.infomgt.dao.nosql.CassandraAbstractModelDao;
import org.iotp.infomgt.dao.util.NoSqlDao;
import org.iotp.infomgt.data.security.AssetCredentials;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.querybuilder.Select.Where;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@NoSqlDao
public class CassandraAssetCredentialsDao extends CassandraAbstractModelDao<AssetCredentialsEntity, AssetCredentials> implements AssetCredentialsDao {

    @Override
    protected Class<AssetCredentialsEntity> getColumnFamilyClass() {
        return AssetCredentialsEntity.class;
    }

    @Override
    protected String getColumnFamilyName() {
        return ModelConstants.ASSET_CREDENTIALS_COLUMN_FAMILY_NAME;
    }

    @Override
    public AssetCredentials findByAssetId(UUID assetId) {
        log.debug("Try to find asset credentials by assetId [{}] ", assetId);
        Where query = select().from(ModelConstants.ASSET_CREDENTIALS_BY_ASSET_COLUMN_FAMILY_NAME)
                .where(eq(ModelConstants.ASSET_CREDENTIALS_ASSET_ID_PROPERTY, assetId));
        log.trace("Execute query {}", query);
        AssetCredentialsEntity assetCredentialsEntity = findOneByStatement(query);
        log.trace("Found asset credentials [{}] by assetId [{}]", assetCredentialsEntity, assetId);
        return DaoUtil.getData(assetCredentialsEntity);
    }
    
    @Override
    public AssetCredentials findByCredentialsId(String credentialsId) {
        log.debug("Try to find asset credentials by credentialsId [{}] ", credentialsId);
        Where query = select().from(ModelConstants.ASSET_CREDENTIALS_BY_CREDENTIALS_ID_COLUMN_FAMILY_NAME)
                .where(eq(ModelConstants.ASSET_CREDENTIALS_CREDENTIALS_ID_PROPERTY, credentialsId));
        log.trace("Execute query {}", query);
        AssetCredentialsEntity assetCredentialsEntity = findOneByStatement(query);
        log.trace("Found asset credentials [{}] by credentialsId [{}]", assetCredentialsEntity, credentialsId);
        return DaoUtil.getData(assetCredentialsEntity);
    }
}

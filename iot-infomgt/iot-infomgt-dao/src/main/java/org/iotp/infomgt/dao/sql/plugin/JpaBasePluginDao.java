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
package org.iotp.infomgt.dao.sql.plugin;

import static org.iotp.infomgt.dao.model.ModelConstants.NULL_UUID_STR;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.sql.PluginMetaDataEntity;
import org.iotp.infomgt.dao.plugin.PluginDao;
import org.iotp.infomgt.dao.sql.JpaAbstractSearchTextDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Valerii Sosliuk on 5/1/2017.
 */
@Slf4j
@Component
@SqlDao
public class JpaBasePluginDao extends JpaAbstractSearchTextDao<PluginMetaDataEntity, PluginMetaData> implements PluginDao {

    @Autowired
    private PluginMetaDataRepository pluginMetaDataRepository;

    @Override
    protected Class<PluginMetaDataEntity> getEntityClass() {
        return PluginMetaDataEntity.class;
    }

    @Override
    protected CrudRepository<PluginMetaDataEntity, String> getCrudRepository() {
        return pluginMetaDataRepository;
    }

    @Override
    public PluginMetaData findById(PluginId pluginId) {
        log.debug("Search plugin meta-data entity by id [{}]", pluginId);
        PluginMetaData pluginMetaData = super.findById(pluginId.getId());
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}] for plugin entity [{}]", pluginMetaData != null, pluginMetaData);
        } else {
            log.debug("Search result: [{}]", pluginMetaData != null);
        }
        return pluginMetaData;
    }

    @Override
    public PluginMetaData findByApiToken(String apiToken) {
        log.debug("Search plugin meta-data entity by api token [{}]", apiToken);
        PluginMetaDataEntity entity = pluginMetaDataRepository.findByApiToken(apiToken);
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}] for plugin entity [{}]", entity != null, entity);
        } else {
            log.debug("Search result: [{}]", entity != null);
        }
        return DaoUtil.getData(entity);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Delete plugin meta-data entity by id [{}]", id);
        pluginMetaDataRepository.delete(UUIDConverter. fromTimeUUID(id));
    }

    @Override
    public void deleteById(PluginId pluginId) {
        deleteById(pluginId.getId());
    }

    @Override
    public List<PluginMetaData> findByTenantIdAndPageLink(TenantId tenantId, TextPageLink pageLink) {
        log.debug("Try to find plugins by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        List<PluginMetaDataEntity> entities = pluginMetaDataRepository
                .findByTenantIdAndPageLink(
                        UUIDConverter.fromTimeUUID(tenantId.getId()),
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? NULL_UUID_STR :  UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        new PageRequest(0, pageLink.getLimit()));
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}]", Arrays.toString(entities.toArray()));
        } else {
            log.debug("Search result: [{}]", entities.size());
        }
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public List<PluginMetaData> findAllTenantPluginsByTenantId(UUID tenantId, TextPageLink pageLink) {
        log.debug("Try to find all tenant plugins by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        List<PluginMetaDataEntity> entities = pluginMetaDataRepository
                .findAllTenantPluginsByTenantId(
                        UUIDConverter.fromTimeUUID(tenantId),
                        NULL_UUID_STR,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? NULL_UUID_STR :  UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        new PageRequest(0, pageLink.getLimit()));
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}]", Arrays.toString(entities.toArray()));
        } else {
            log.debug("Search result: [{}]", entities.size());
        }
        return DaoUtil.convertDataList(entities);
    }
}

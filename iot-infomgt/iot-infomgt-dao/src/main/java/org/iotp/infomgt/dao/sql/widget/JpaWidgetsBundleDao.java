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
package org.iotp.infomgt.dao.sql.widget;

import static org.iotp.infomgt.dao.model.ModelConstants.NULL_UUID_STR;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.sql.WidgetsBundleEntity;
import org.iotp.infomgt.dao.sql.JpaAbstractSearchTextDao;
import org.iotp.infomgt.dao.util.SqlDao;
import org.iotp.infomgt.dao.widget.WidgetsBundleDao;
import org.iotp.infomgt.data.common.UUIDConverter;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.widget.WidgetsBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Valerii Sosliuk on 4/23/2017.
 */
@Component
@SqlDao
public class JpaWidgetsBundleDao extends JpaAbstractSearchTextDao<WidgetsBundleEntity, WidgetsBundle> implements WidgetsBundleDao {

    @Autowired
    private WidgetsBundleRepository widgetsBundleRepository;

    @Override
    protected Class<WidgetsBundleEntity> getEntityClass() {
        return WidgetsBundleEntity.class;
    }

    @Override
    protected CrudRepository<WidgetsBundleEntity, String> getCrudRepository() {
        return widgetsBundleRepository;
    }

    @Override
    public WidgetsBundle findWidgetsBundleByTenantIdAndAlias(UUID tenantId, String alias) {
        return DaoUtil.getData(widgetsBundleRepository.findWidgetsBundleByTenantIdAndAlias(UUIDConverter.fromTimeUUID(tenantId), alias));
    }

    @Override
    public List<WidgetsBundle> findSystemWidgetsBundles(TextPageLink pageLink) {
        return DaoUtil.convertDataList(
                widgetsBundleRepository
                        .findSystemWidgetsBundles(
                                NULL_UUID_STR,
                                Objects.toString(pageLink.getTextSearch(), ""),
                                pageLink.getIdOffset() == null ? NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                                new PageRequest(0, pageLink.getLimit())));
    }

    @Override
    public List<WidgetsBundle> findTenantWidgetsBundlesByTenantId(UUID tenantId, TextPageLink pageLink) {
        return DaoUtil.convertDataList(
                widgetsBundleRepository
                        .findTenantWidgetsBundlesByTenantId(
                                UUIDConverter.fromTimeUUID(tenantId),
                                Objects.toString(pageLink.getTextSearch(), ""),
                                pageLink.getIdOffset() == null ? NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                                new PageRequest(0, pageLink.getLimit())));
    }

    @Override
    public List<WidgetsBundle> findAllTenantWidgetsBundlesByTenantId(UUID tenantId, TextPageLink pageLink) {
        return DaoUtil.convertDataList(
                widgetsBundleRepository
                        .findAllTenantWidgetsBundlesByTenantId(
                                UUIDConverter.fromTimeUUID(tenantId),
                                NULL_UUID_STR,
                                Objects.toString(pageLink.getTextSearch(), ""),
                                pageLink.getIdOffset() == null ? NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                                new PageRequest(0, pageLink.getLimit())));
    }
}

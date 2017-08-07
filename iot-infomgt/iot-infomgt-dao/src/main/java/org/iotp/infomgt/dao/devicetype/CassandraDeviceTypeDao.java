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
package org.iotp.infomgt.dao.devicetype;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPES_BY_TENANT_VIEW_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_BY_CUSTOMER_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_BY_CUSTOMER_BY_TYPE_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_BY_TENANT_AND_NAME_VIEW_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_BY_TENANT_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_BY_TENANT_BY_TYPE_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_CUSTOMER_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_NAME_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_TENANT_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.DEVICE_TYPE_TYPE_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.ID_PROPERTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.TenantDeviceTypeEntity;
import org.iotp.infomgt.dao.model.nosql.DeviceTypeEntity;
import org.iotp.infomgt.dao.nosql.CassandraAbstractSearchTextDao;
import org.iotp.infomgt.dao.util.NoSqlDao;
import org.iotp.infomgt.data.DeviceType;
import org.iotp.infomgt.data.TenantDeviceType;
import org.iotp.infomgt.data.page.TextPageLink;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@NoSqlDao
public class CassandraDeviceTypeDao extends CassandraAbstractSearchTextDao<DeviceTypeEntity, DeviceType> implements DeviceTypeDao {

    @Override
    protected Class<DeviceTypeEntity> getColumnFamilyClass() {
        return DeviceTypeEntity.class;
    }

    @Override
    protected String getColumnFamilyName() {
        return DEVICE_TYPE_COLUMN_FAMILY_NAME;
    }

    @Override
    public List<DeviceType> findDeviceTypesByTenantId(UUID tenantId, TextPageLink pageLink) {
        log.debug("Try to find devicetypes by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        List<DeviceTypeEntity> deviceEntities = findPageWithTextSearch(DEVICE_TYPE_BY_TENANT_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Collections.singletonList(eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId)), pageLink);

        log.trace("Found devicetypes [{}] by tenantId [{}] and pageLink [{}]", deviceEntities, tenantId, pageLink);
        return DaoUtil.convertDataList(deviceEntities);
    }

    @Override
    public List<DeviceType> findDeviceTypesByTenantIdAndTypeName(UUID tenantId, String type, TextPageLink pageLink) {
        log.debug("Try to find devices by tenantId [{}], type [{}] and pageLink [{}]", tenantId, type, pageLink);
        List<DeviceTypeEntity> deviceEntities = findPageWithTextSearch(DEVICE_TYPE_BY_TENANT_BY_TYPE_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(DEVICE_TYPE_TYPE_PROPERTY, type),
                        eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId)), pageLink);
        log.trace("Found devices [{}] by tenantId [{}], type [{}] and pageLink [{}]", deviceEntities, tenantId, type, pageLink);
        return DaoUtil.convertDataList(deviceEntities);
    }

    @Override
    public ListenableFuture<List<DeviceType>> findDeviceTypesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> deviceIds) {
        log.debug("Try to find devices by tenantId [{}] and device Ids [{}]", tenantId, deviceIds);
        Select select = select().from(getColumnFamilyName());
        Select.Where query = select.where();
        query.and(eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId));
        query.and(in(ID_PROPERTY, deviceIds));
        return findListByStatementAsync(query);
    }

    @Override
    public List<DeviceType> findDeviceTypesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, TextPageLink pageLink) {
        log.debug("Try to find devices by tenantId [{}], customerId[{}] and pageLink [{}]", tenantId, customerId, pageLink);
        List<DeviceTypeEntity> deviceEntities = findPageWithTextSearch(DEVICE_TYPE_BY_CUSTOMER_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(DEVICE_TYPE_CUSTOMER_ID_PROPERTY, customerId),
                        eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId)),
                pageLink);

        log.trace("Found devices [{}] by tenantId [{}], customerId [{}] and pageLink [{}]", deviceEntities, tenantId, customerId, pageLink);
        return DaoUtil.convertDataList(deviceEntities);
    }

    @Override
    public List<DeviceType> findDeviceTypesByTenantIdAndCustomerIdAndTypeName(UUID tenantId, UUID customerId, String type, TextPageLink pageLink) {
        log.debug("Try to find devices by tenantId [{}], customerId [{}], type [{}] and pageLink [{}]", tenantId, customerId, type, pageLink);
        List<DeviceTypeEntity> deviceEntities = findPageWithTextSearch(DEVICE_TYPE_BY_CUSTOMER_BY_TYPE_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(DEVICE_TYPE_TYPE_PROPERTY, type),
                        eq(DEVICE_TYPE_CUSTOMER_ID_PROPERTY, customerId),
                        eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId)),
                pageLink);

        log.trace("Found devices [{}] by tenantId [{}], customerId [{}], type [{}] and pageLink [{}]", deviceEntities, tenantId, customerId, type, pageLink);
        return DaoUtil.convertDataList(deviceEntities);
    }

    @Override
    public ListenableFuture<List<DeviceType>> findDeviceTypesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> deviceIds) {
        log.debug("Try to find devices by tenantId [{}], customerId [{}] and device Ids [{}]", tenantId, customerId, deviceIds);
        Select select = select().from(getColumnFamilyName());
        Select.Where query = select.where();
        query.and(eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId));
        query.and(eq(DEVICE_TYPE_CUSTOMER_ID_PROPERTY, customerId));
        query.and(in(ID_PROPERTY, deviceIds));
        return findListByStatementAsync(query);
    }

    @Override
    public Optional<DeviceType> findDeviceTypeByTenantIdAndName(UUID tenantId, String deviceName) {
        Select select = select().from(DEVICE_TYPE_BY_TENANT_AND_NAME_VIEW_NAME);
        Select.Where query = select.where();
        query.and(eq(DEVICE_TYPE_TENANT_ID_PROPERTY, tenantId));
        query.and(eq(DEVICE_TYPE_NAME_PROPERTY, deviceName));
        return Optional.ofNullable(DaoUtil.getData(findOneByStatement(query)));
    }

    @Override
    public ListenableFuture<List<TenantDeviceType>> findTenantDeviceTypesAsync() {
        Select statement = select().distinct().column(DEVICE_TYPE_TYPE_PROPERTY).column(DEVICE_TYPE_TENANT_ID_PROPERTY).from(DEVICE_TYPES_BY_TENANT_VIEW_NAME);
        statement.setConsistencyLevel(cluster.getDefaultReadConsistencyLevel());
        ResultSetFuture resultSetFuture = getSession().executeAsync(statement);
        ListenableFuture<List<TenantDeviceTypeEntity>> result = Futures.transform(resultSetFuture, new Function<ResultSet, List<TenantDeviceTypeEntity>>() {
            @Nullable
            @Override
            public List<TenantDeviceTypeEntity> apply(@Nullable ResultSet resultSet) {
                Result<TenantDeviceTypeEntity> result = cluster.getMapper(TenantDeviceTypeEntity.class).map(resultSet);
                if (result != null) {
                    return result.all();
                } else {
                    return Collections.emptyList();
                }
            }
        });
        return Futures.transform(result, new Function<List<TenantDeviceTypeEntity>, List<TenantDeviceType>>() {
            @Nullable
            @Override
            public List<TenantDeviceType> apply(@Nullable List<TenantDeviceTypeEntity> entityList) {
                List<TenantDeviceType> list = Collections.emptyList();
                if (entityList != null && !entityList.isEmpty()) {
                    list = new ArrayList<>();
                    for (TenantDeviceTypeEntity object : entityList) {
                        list.add(object.toTenantDeviceType());
                    }
                }
                return list;
            }
        });
    }

}

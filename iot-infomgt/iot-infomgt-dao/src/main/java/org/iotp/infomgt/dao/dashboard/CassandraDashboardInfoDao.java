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
package org.iotp.infomgt.dao.dashboard;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static org.iotp.infomgt.dao.model.ModelConstants.DASHBOARD_BY_CUSTOMER_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DASHBOARD_BY_TENANT_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DASHBOARD_COLUMN_FAMILY_NAME;
import static org.iotp.infomgt.dao.model.ModelConstants.DASHBOARD_CUSTOMER_ID_PROPERTY;
import static org.iotp.infomgt.dao.model.ModelConstants.DASHBOARD_TENANT_ID_PROPERTY;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.iotp.infomgt.dao.DaoUtil;
import org.iotp.infomgt.dao.model.nosql.DashboardInfoEntity;
import org.iotp.infomgt.dao.nosql.CassandraAbstractSearchTextDao;
import org.iotp.infomgt.dao.util.NoSqlDao;
import org.iotp.infomgt.data.DashboardInfo;
import org.iotp.infomgt.data.page.TextPageLink;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@NoSqlDao
public class CassandraDashboardInfoDao extends CassandraAbstractSearchTextDao<DashboardInfoEntity, DashboardInfo> implements DashboardInfoDao {

    @Override
    protected Class<DashboardInfoEntity> getColumnFamilyClass() {
        return DashboardInfoEntity.class;
    }

    @Override
    protected String getColumnFamilyName() {
        return DASHBOARD_COLUMN_FAMILY_NAME;
    }

    @Override
    public List<DashboardInfo> findDashboardsByTenantId(UUID tenantId, TextPageLink pageLink) {
        log.debug("Try to find dashboards by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        List<DashboardInfoEntity> dashboardEntities = findPageWithTextSearch(DASHBOARD_BY_TENANT_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Collections.singletonList(eq(DASHBOARD_TENANT_ID_PROPERTY, tenantId)),
                pageLink);

        log.trace("Found dashboards [{}] by tenantId [{}] and pageLink [{}]", dashboardEntities, tenantId, pageLink);
        return DaoUtil.convertDataList(dashboardEntities);
    }

    @Override
    public List<DashboardInfo> findDashboardsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, TextPageLink pageLink) {
        log.debug("Try to find dashboards by tenantId [{}], customerId[{}] and pageLink [{}]", tenantId, customerId, pageLink);
        List<DashboardInfoEntity> dashboardEntities = findPageWithTextSearch(DASHBOARD_BY_CUSTOMER_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(DASHBOARD_CUSTOMER_ID_PROPERTY, customerId),
                        eq(DASHBOARD_TENANT_ID_PROPERTY, tenantId)),
                pageLink);

        log.trace("Found dashboards [{}] by tenantId [{}], customerId [{}] and pageLink [{}]", dashboardEntities, tenantId, customerId, pageLink);
        return DaoUtil.convertDataList(dashboardEntities);
    }

}

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

import static org.iotp.infomgt.dao.util.Validator.validateId;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.iotp.infomgt.dao.customer.CustomerDao;
import org.iotp.infomgt.dao.entity.AbstractEntityService;
import org.iotp.infomgt.dao.exception.DataValidationException;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.dao.tenant.TenantDao;
import org.iotp.infomgt.dao.util.DataValidator;
import org.iotp.infomgt.dao.util.PaginatedRemover;
import org.iotp.infomgt.dao.util.Validator;
import org.iotp.infomgt.data.Customer;
import org.iotp.infomgt.data.Dashboard;
import org.iotp.infomgt.data.DashboardInfo;
import org.iotp.infomgt.data.Tenant;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DashboardId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardServiceImpl extends AbstractEntityService implements DashboardService {

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardInfoDao dashboardInfoDao;

    @Autowired
    private TenantDao tenantDao;
    
    @Autowired
    private CustomerDao customerDao;
    
    @Override
    public Dashboard findDashboardById(DashboardId dashboardId) {
        log.trace("Executing findDashboardById [{}]", dashboardId);
        Validator.validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
        return dashboardDao.findById(dashboardId.getId());
    }

    @Override
    public ListenableFuture<Dashboard> findDashboardByIdAsync(DashboardId dashboardId) {
        log.trace("Executing findDashboardByIdAsync [{}]", dashboardId);
        validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
        return dashboardDao.findByIdAsync(dashboardId.getId());
    }

    @Override
    public DashboardInfo findDashboardInfoById(DashboardId dashboardId) {
        log.trace("Executing findDashboardInfoById [{}]", dashboardId);
        Validator.validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
        return dashboardInfoDao.findById(dashboardId.getId());
    }

    @Override
    public ListenableFuture<DashboardInfo> findDashboardInfoByIdAsync(DashboardId dashboardId) {
        log.trace("Executing findDashboardInfoByIdAsync [{}]", dashboardId);
        validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
        return dashboardInfoDao.findByIdAsync(dashboardId.getId());
    }

    @Override
    public Dashboard saveDashboard(Dashboard dashboard) {
        log.trace("Executing saveDashboard [{}]", dashboard);
        dashboardValidator.validate(dashboard);
        return dashboardDao.save(dashboard);
    }
    
    @Override
    public Dashboard assignDashboardToCustomer(DashboardId dashboardId, CustomerId customerId) {
        Dashboard dashboard = findDashboardById(dashboardId);
        dashboard.setCustomerId(customerId);
        return saveDashboard(dashboard);
    }

    @Override
    public Dashboard unassignDashboardFromCustomer(DashboardId dashboardId) {
        Dashboard dashboard = findDashboardById(dashboardId);
        dashboard.setCustomerId(null);
        return saveDashboard(dashboard);
    }

    @Override
    public void deleteDashboard(DashboardId dashboardId) {
        log.trace("Executing deleteDashboard [{}]", dashboardId);
        Validator.validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
        deleteEntityRelations(dashboardId);
        dashboardDao.removeById(dashboardId.getId());
    }

    @Override
    public TextPageData<DashboardInfo> findDashboardsByTenantId(TenantId tenantId, TextPageLink pageLink) {
        log.trace("Executing findDashboardsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        Validator.validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<DashboardInfo> dashboards = dashboardInfoDao.findDashboardsByTenantId(tenantId.getId(), pageLink);
        return new TextPageData<>(dashboards, pageLink);
    }

    @Override
    public void deleteDashboardsByTenantId(TenantId tenantId) {
        log.trace("Executing deleteDashboardsByTenantId, tenantId [{}]", tenantId);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        tenantDashboardsRemover.removeEntities(tenantId);
    }

    @Override
    public TextPageData<DashboardInfo> findDashboardsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink) {
        log.trace("Executing findDashboardsByTenantIdAndCustomerId, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        Validator.validateId(customerId, "Incorrect customerId " + customerId);
        Validator.validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<DashboardInfo> dashboards = dashboardInfoDao.findDashboardsByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
        return new TextPageData<>(dashboards, pageLink);
    }

    @Override
    public void unassignCustomerDashboards(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing unassignCustomerDashboards, tenantId [{}], customerId [{}]", tenantId, customerId);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        Validator.validateId(customerId, "Incorrect customerId " + customerId);
        new CustomerDashboardsUnassigner(tenantId).removeEntities(customerId);
    }
    
    private DataValidator<Dashboard> dashboardValidator =
            new DataValidator<Dashboard>() {
                @Override
                protected void validateDataImpl(Dashboard dashboard) {
                    if (StringUtils.isEmpty(dashboard.getTitle())) {
                        throw new DataValidationException("Dashboard title should be specified!");
                    }
                    if (dashboard.getTenantId() == null) {
                        throw new DataValidationException("Dashboard should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(dashboard.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("Dashboard is referencing to non-existent tenant!");
                        }
                    }
                    if (dashboard.getCustomerId() == null) {
                        dashboard.setCustomerId(new CustomerId(ModelConstants.NULL_UUID));
                    } else if (!dashboard.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
                        Customer customer = customerDao.findById(dashboard.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("Can't assign dashboard to non-existent customer!");
                        }
                        if (!customer.getTenantId().getId().equals(dashboard.getTenantId().getId())) {
                            throw new DataValidationException("Can't assign dashboard to customer from different tenant!");
                        }
                    }
                }
    };
    
    private PaginatedRemover<TenantId, DashboardInfo> tenantDashboardsRemover =
            new PaginatedRemover<TenantId, DashboardInfo>() {
        
        @Override
        protected List<DashboardInfo> findEntities(TenantId id, TextPageLink pageLink) {
            return dashboardInfoDao.findDashboardsByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(DashboardInfo entity) {
            deleteDashboard(new DashboardId(entity.getUuidId()));
        }
    };
    
    private class CustomerDashboardsUnassigner extends PaginatedRemover<CustomerId, DashboardInfo> {
        
        private TenantId tenantId;
        
        CustomerDashboardsUnassigner(TenantId tenantId) {
            this.tenantId = tenantId;
        }

        @Override
        protected List<DashboardInfo> findEntities(CustomerId id, TextPageLink pageLink) {
            return dashboardInfoDao.findDashboardsByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(DashboardInfo entity) {
            unassignDashboardFromCustomer(new DashboardId(entity.getUuidId()));
        }
        
    }

}

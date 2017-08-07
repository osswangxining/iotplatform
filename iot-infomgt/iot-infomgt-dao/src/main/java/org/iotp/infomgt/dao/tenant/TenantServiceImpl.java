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
package org.iotp.infomgt.dao.tenant;

import static org.iotp.infomgt.dao.util.Validator.validateId;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.iotp.infomgt.dao.asset.AssetService;
import org.iotp.infomgt.dao.customer.CustomerService;
import org.iotp.infomgt.dao.dashboard.DashboardService;
import org.iotp.infomgt.dao.device.DeviceService;
import org.iotp.infomgt.dao.entity.AbstractEntityService;
import org.iotp.infomgt.dao.exception.DataValidationException;
import org.iotp.infomgt.dao.plugin.PluginService;
import org.iotp.infomgt.dao.rule.RuleService;
import org.iotp.infomgt.dao.user.UserService;
import org.iotp.infomgt.dao.util.DataValidator;
import org.iotp.infomgt.dao.util.PaginatedRemover;
import org.iotp.infomgt.dao.util.Validator;
import org.iotp.infomgt.dao.widget.WidgetsBundleService;
import org.iotp.infomgt.data.Tenant;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TenantServiceImpl extends AbstractEntityService implements TenantService {

  private static final String DEFAULT_TENANT_REGION = "Global";

  @Autowired
  private TenantDao tenantDao;

  @Autowired
  private UserService userService;

  @Autowired
  private CustomerService customerService;

  @Autowired
  private AssetService assetService;

  @Autowired
  private DeviceService deviceService;

  @Autowired
  private WidgetsBundleService widgetsBundleService;

  @Autowired
  private DashboardService dashboardService;

  @Autowired
  private RuleService ruleService;

  @Autowired
  private PluginService pluginService;

  @Override
  public Tenant findTenantById(TenantId tenantId) {
    log.trace("Executing findTenantById [{}]", tenantId);
    Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
    return tenantDao.findById(tenantId.getId());
  }

  @Override
  public ListenableFuture<Tenant> findTenantByIdAsync(TenantId tenantId) {
    log.trace("Executing TenantIdAsync [{}]", tenantId);
    validateId(tenantId, "Incorrect tenantId " + tenantId);
    return tenantDao.findByIdAsync(tenantId.getId());
  }

  @Override
  public Tenant saveTenant(Tenant tenant) {
    log.trace("Executing saveTenant [{}]", tenant);
    tenant.setRegion(DEFAULT_TENANT_REGION);
    tenantValidator.validate(tenant);
    return tenantDao.save(tenant);
  }

  @Override
  public void deleteTenant(TenantId tenantId) {
    log.trace("Executing deleteTenant [{}]", tenantId);
    Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
    customerService.deleteCustomersByTenantId(tenantId);
    widgetsBundleService.deleteWidgetsBundlesByTenantId(tenantId);
    dashboardService.deleteDashboardsByTenantId(tenantId);
    assetService.deleteAssetsByTenantId(tenantId);
    deviceService.deleteDevicesByTenantId(tenantId);
    userService.deleteTenantAdmins(tenantId);
    ruleService.deleteRulesByTenantId(tenantId);
    pluginService.deletePluginsByTenantId(tenantId);
    tenantDao.removeById(tenantId.getId());
    deleteEntityRelations(tenantId);
  }

  @Override
  public TextPageData<Tenant> findTenants(TextPageLink pageLink) {
    log.trace("Executing findTenants pageLink [{}]", pageLink);
    Validator.validatePageLink(pageLink, "Incorrect page link " + pageLink);
    List<Tenant> tenants = tenantDao.findTenantsByRegion(DEFAULT_TENANT_REGION, pageLink);
    return new TextPageData<>(tenants, pageLink);
  }

  @Override
  public void deleteTenants() {
    log.trace("Executing deleteTenants");
    tenantsRemover.removeEntities(DEFAULT_TENANT_REGION);
  }

  private DataValidator<Tenant> tenantValidator = new DataValidator<Tenant>() {
    @Override
    protected void validateDataImpl(Tenant tenant) {
      if (StringUtils.isEmpty(tenant.getTitle())) {
        throw new DataValidationException("Tenant title should be specified!");
      }
      if (!StringUtils.isEmpty(tenant.getEmail())) {
        validateEmail(tenant.getEmail());
      }
    }
  };

  private PaginatedRemover<String, Tenant> tenantsRemover = new PaginatedRemover<String, Tenant>() {

    @Override
    protected List<Tenant> findEntities(String region, TextPageLink pageLink) {
      return tenantDao.findTenantsByRegion(region, pageLink);
    }

    @Override
    protected void removeEntity(Tenant entity) {
      deleteTenant(new TenantId(entity.getUuidId()));
    }
  };
}

package org.iotp.server.controller;

import static org.iotp.infomgt.dao.util.Validator.validateId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.iotp.infomgt.dao.alarm.AlarmService;
import org.iotp.infomgt.dao.asset.AssetCredentialsService;
import org.iotp.infomgt.dao.asset.AssetService;
import org.iotp.infomgt.dao.customer.CustomerService;
import org.iotp.infomgt.dao.dashboard.DashboardService;
import org.iotp.infomgt.dao.device.DeviceCredentialsService;
import org.iotp.infomgt.dao.device.DeviceService;
import org.iotp.infomgt.dao.devicetype.DeviceTypeService;
import org.iotp.infomgt.dao.exception.DataValidationException;
import org.iotp.infomgt.dao.exception.IncorrectParameterException;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.dao.plugin.PluginService;
import org.iotp.infomgt.dao.relation.RelationService;
import org.iotp.infomgt.dao.rule.RuleService;
import org.iotp.infomgt.dao.user.UserService;
import org.iotp.infomgt.dao.widget.WidgetTypeService;
import org.iotp.infomgt.dao.widget.WidgetsBundleService;
import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.Customer;
import org.iotp.infomgt.data.Dashboard;
import org.iotp.infomgt.data.DashboardInfo;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.DeviceType;
import org.iotp.infomgt.data.User;
import org.iotp.infomgt.data.alarm.Alarm;
import org.iotp.infomgt.data.alarm.AlarmInfo;
import org.iotp.infomgt.data.id.AlarmId;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DashboardId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.DeviceTypeId;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.PluginId;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.id.UserId;
import org.iotp.infomgt.data.id.WidgetTypeId;
import org.iotp.infomgt.data.id.WidgetsBundleId;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.page.TimePageLink;
import org.iotp.infomgt.data.plugin.ComponentDescriptor;
import org.iotp.infomgt.data.plugin.ComponentType;
import org.iotp.infomgt.data.plugin.PluginMetaData;
import org.iotp.infomgt.data.rule.RuleMetaData;
import org.iotp.infomgt.data.security.Authority;
import org.iotp.infomgt.data.widget.WidgetType;
import org.iotp.infomgt.data.widget.WidgetsBundle;
import org.iotp.server.exception.IoTPErrorCode;
import org.iotp.server.exception.IoTPErrorResponseHandler;
import org.iotp.server.exception.IoTPException;
import org.iotp.server.msghub.KafkaMsgProducer;
import org.iotp.server.service.component.ComponentDiscoveryService;
import org.iotp.server.service.security.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController {

  @Autowired
  private IoTPErrorResponseHandler errorResponseHandler;

  @Autowired
  protected CustomerService customerService;

  @Autowired
  protected UserService userService;

  @Autowired
  protected DeviceService deviceService;

  @Autowired
  protected DeviceTypeService deviceTypeService;

  @Autowired
  protected AssetService assetService;

  @Autowired
  protected AlarmService alarmService;

  @Autowired
  protected DeviceCredentialsService deviceCredentialsService;

  @Autowired
  protected AssetCredentialsService assetCredentialsService;

  @Autowired
  protected WidgetsBundleService widgetsBundleService;

  @Autowired
  protected WidgetTypeService widgetTypeService;

  @Autowired
  protected DashboardService dashboardService;

  @Autowired
  protected ComponentDiscoveryService componentDescriptorService;

  @Autowired
  protected RuleService ruleService;

  @Autowired
  protected PluginService pluginService;

  // @Autowired
  // protected ActorService actorService;

  @Autowired
  protected RelationService relationService;
  @Autowired
  protected KafkaMsgProducer msgProducer;

  @ExceptionHandler(IoTPException.class)
  public void handleThingsboardException(IoTPException ex, HttpServletResponse response) {
    errorResponseHandler.handle(ex, response);
  }

  IoTPException handleException(Exception exception) {
    return handleException(exception, true);
  }

  private IoTPException handleException(Exception exception, boolean logException) {
    if (logException) {
      log.error("Error [{}]", exception.getMessage());
    }

    String cause = "";
    if (exception.getCause() != null) {
      cause = exception.getCause().getClass().getCanonicalName();
    }

    if (exception instanceof IoTPException) {
      return (IoTPException) exception;
    } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
        || exception instanceof DataValidationException || cause.contains("IncorrectParameterException")) {
      return new IoTPException(exception.getMessage(), IoTPErrorCode.BAD_REQUEST_PARAMS);
    } else if (exception instanceof MessagingException) {
      return new IoTPException("Unable to send mail: " + exception.getMessage(), IoTPErrorCode.GENERAL);
    } else {
      return new IoTPException(exception.getMessage(), IoTPErrorCode.GENERAL);
    }
  }

  <T> T checkNotNull(T reference) throws IoTPException {
    if (reference == null) {
      throw new IoTPException("Requested item wasn't found!", IoTPErrorCode.ITEM_NOT_FOUND);
    }
    return reference;
  }

  <T> T checkNotNull(Optional<T> reference) throws IoTPException {
    if (reference.isPresent()) {
      return reference.get();
    } else {
      throw new IoTPException("Requested item wasn't found!", IoTPErrorCode.ITEM_NOT_FOUND);
    }
  }

  void checkParameter(String name, String param) throws IoTPException {
    if (StringUtils.isEmpty(param)) {
      throw new IoTPException("Parameter '" + name + "' can't be empty!", IoTPErrorCode.BAD_REQUEST_PARAMS);
    }
  }

  void checkArrayParameter(String name, String[] params) throws IoTPException {
    if (params == null || params.length == 0) {
      throw new IoTPException("Parameter '" + name + "' can't be empty!", IoTPErrorCode.BAD_REQUEST_PARAMS);
    } else {
      for (String param : params) {
        checkParameter(name, param);
      }
    }
  }

  UUID toUUID(String id) {
    return UUID.fromString(id);
  }

  TimePageLink createPageLink(int limit, Long startTime, Long endTime, boolean ascOrder, String idOffset) {
    UUID idOffsetUuid = null;
    if (StringUtils.isNotEmpty(idOffset)) {
      idOffsetUuid = toUUID(idOffset);
    }
    return new TimePageLink(limit, startTime, endTime, ascOrder, idOffsetUuid);
  }

  TextPageLink createPageLink(int limit, String textSearch, String idOffset, String textOffset) {
    UUID idOffsetUuid = null;
    if (StringUtils.isNotEmpty(idOffset)) {
      idOffsetUuid = toUUID(idOffset);
    }
    return new TextPageLink(limit, textSearch, idOffsetUuid, textOffset);
  }

  protected SecurityUser getCurrentUser() throws IoTPException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
      return (SecurityUser) authentication.getPrincipal();
    } else {
      throw new IoTPException("You aren't authorized to perform this operation!", IoTPErrorCode.AUTHENTICATION);
    }
  }

  void checkTenantId(TenantId tenantId) throws IoTPException {
    validateId(tenantId, "Incorrect tenantId " + tenantId);
    SecurityUser authUser = getCurrentUser();
    if (authUser.getAuthority() != Authority.SYS_ADMIN
        && (authUser.getTenantId() == null || !authUser.getTenantId().equals(tenantId))) {
      throw new IoTPException("You don't have permission to perform this operation!", IoTPErrorCode.PERMISSION_DENIED);
    }
  }

  protected TenantId getTenantId() throws IoTPException {
    return getCurrentUser().getTenantId();
  }

  Customer checkCustomerId(CustomerId customerId) throws IoTPException {
    try {
      validateId(customerId, "Incorrect customerId " + customerId);
      SecurityUser authUser = getCurrentUser();
      if (authUser.getAuthority() == Authority.SYS_ADMIN || (authUser.getAuthority() != Authority.TENANT_ADMIN
          && (authUser.getCustomerId() == null || !authUser.getCustomerId().equals(customerId)))) {
        throw new IoTPException("You don't have permission to perform this operation!",
            IoTPErrorCode.PERMISSION_DENIED);
      }
      Customer customer = customerService.findCustomerById(customerId);
      checkCustomer(customer);
      return customer;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  private void checkCustomer(Customer customer) throws IoTPException {
    checkNotNull(customer);
    checkTenantId(customer.getTenantId());
  }

  User checkUserId(UserId userId) throws IoTPException {
    try {
      validateId(userId, "Incorrect userId " + userId);
      User user = userService.findUserById(userId);
      checkUser(user);
      return user;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  private void checkUser(User user) throws IoTPException {
    checkNotNull(user);
    checkTenantId(user.getTenantId());
    if (user.getAuthority() == Authority.CUSTOMER_USER) {
      checkCustomerId(user.getCustomerId());
    }
  }

  protected void checkEntityId(EntityId entityId) throws IoTPException {
    try {
      checkNotNull(entityId);
      validateId(entityId.getId(), "Incorrect entityId " + entityId);
      switch (entityId.getEntityType()) {
      case DEVICE:
        checkDevice(deviceService.findDeviceById(new DeviceId(entityId.getId())));
        return;
      case CUSTOMER:
        checkCustomerId(new CustomerId(entityId.getId()));
        return;
      case TENANT:
        checkTenantId(new TenantId(entityId.getId()));
        return;
      case PLUGIN:
        checkPlugin(new PluginId(entityId.getId()));
        return;
      case RULE:
        checkRule(new RuleId(entityId.getId()));
        return;
      case ASSET:
        checkAsset(assetService.findAssetById(new AssetId(entityId.getId())));
        return;
      case DASHBOARD:
        checkDashboardId(new DashboardId(entityId.getId()));
        return;
      case USER:
        checkUserId(new UserId(entityId.getId()));
        return;
      default:
        throw new IllegalArgumentException("Unsupported entity type: " + entityId.getEntityType());
      }
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  Device checkDeviceId(DeviceId deviceId) throws IoTPException {
    try {
      validateId(deviceId, "Incorrect deviceId " + deviceId);
      Device device = deviceService.findDeviceById(deviceId);
      checkDevice(device);
      return device;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  DeviceType checkDeviceTypeId(DeviceTypeId deviceId) throws IoTPException {
    try {
      validateId(deviceId, "Incorrect deviceId " + deviceId);

      DeviceType deviceType = deviceTypeService.findDeviceById(deviceId);
      checkDeviceType(deviceType);
      return deviceType;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  protected void checkDeviceType(DeviceType deviceType) throws IoTPException {
    checkNotNull(deviceType);
    checkTenantId(deviceType.getTenantId());
    if (deviceType.getCustomerId() != null && !deviceType.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
      checkCustomerId(deviceType.getCustomerId());
    }
  }

  protected void checkDevice(Device device) throws IoTPException {
    checkNotNull(device);
    checkTenantId(device.getTenantId());
    if (device.getCustomerId() != null && !device.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
      checkCustomerId(device.getCustomerId());
    }
  }

  Asset checkAssetId(AssetId assetId) throws IoTPException {
    try {
      validateId(assetId, "Incorrect assetId " + assetId);
      Asset asset = assetService.findAssetById(assetId);
      checkAsset(asset);
      return asset;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  protected void checkAsset(Asset asset) throws IoTPException {
    checkNotNull(asset);
    checkTenantId(asset.getTenantId());
    if (asset.getCustomerId() != null && !asset.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
      checkCustomerId(asset.getCustomerId());
    }
  }

  Alarm checkAlarmId(AlarmId alarmId) throws IoTPException {
    try {
      validateId(alarmId, "Incorrect alarmId " + alarmId);
      Alarm alarm = alarmService.findAlarmByIdAsync(alarmId).get();
      checkAlarm(alarm);
      return alarm;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  AlarmInfo checkAlarmInfoId(AlarmId alarmId) throws IoTPException {
    try {
      validateId(alarmId, "Incorrect alarmId " + alarmId);
      AlarmInfo alarmInfo = alarmService.findAlarmInfoByIdAsync(alarmId).get();
      checkAlarm(alarmInfo);
      return alarmInfo;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  protected void checkAlarm(Alarm alarm) throws IoTPException {
    checkNotNull(alarm);
    checkTenantId(alarm.getTenantId());
  }

  WidgetsBundle checkWidgetsBundleId(WidgetsBundleId widgetsBundleId, boolean modify) throws IoTPException {
    try {
      validateId(widgetsBundleId, "Incorrect widgetsBundleId " + widgetsBundleId);
      WidgetsBundle widgetsBundle = widgetsBundleService.findWidgetsBundleById(widgetsBundleId);
      checkWidgetsBundle(widgetsBundle, modify);
      return widgetsBundle;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  private void checkWidgetsBundle(WidgetsBundle widgetsBundle, boolean modify) throws IoTPException {
    checkNotNull(widgetsBundle);
    if (widgetsBundle.getTenantId() != null && !widgetsBundle.getTenantId().getId().equals(ModelConstants.NULL_UUID)) {
      checkTenantId(widgetsBundle.getTenantId());
    } else if (modify && getCurrentUser().getAuthority() != Authority.SYS_ADMIN) {
      throw new IoTPException("You don't have permission to perform this operation!", IoTPErrorCode.PERMISSION_DENIED);
    }
  }

  WidgetType checkWidgetTypeId(WidgetTypeId widgetTypeId, boolean modify) throws IoTPException {
    try {
      validateId(widgetTypeId, "Incorrect widgetTypeId " + widgetTypeId);
      WidgetType widgetType = widgetTypeService.findWidgetTypeById(widgetTypeId);
      checkWidgetType(widgetType, modify);
      return widgetType;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  void checkWidgetType(WidgetType widgetType, boolean modify) throws IoTPException {
    checkNotNull(widgetType);
    if (widgetType.getTenantId() != null && !widgetType.getTenantId().getId().equals(ModelConstants.NULL_UUID)) {
      checkTenantId(widgetType.getTenantId());
    } else if (modify && getCurrentUser().getAuthority() != Authority.SYS_ADMIN) {
      throw new IoTPException("You don't have permission to perform this operation!", IoTPErrorCode.PERMISSION_DENIED);
    }
  }

  Dashboard checkDashboardId(DashboardId dashboardId) throws IoTPException {
    try {
      validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
      Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
      checkDashboard(dashboard, true);
      return dashboard;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  DashboardInfo checkDashboardInfoId(DashboardId dashboardId) throws IoTPException {
    try {
      validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
      DashboardInfo dashboardInfo = dashboardService.findDashboardInfoById(dashboardId);
      SecurityUser authUser = getCurrentUser();
      checkDashboard(dashboardInfo, authUser.getAuthority() != Authority.SYS_ADMIN);
      return dashboardInfo;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  private void checkDashboard(DashboardInfo dashboard, boolean checkCustomerId) throws IoTPException {
    checkNotNull(dashboard);
    checkTenantId(dashboard.getTenantId());
    SecurityUser authUser = getCurrentUser();
    if (authUser.getAuthority() == Authority.CUSTOMER_USER) {
      if (dashboard.getCustomerId() == null || dashboard.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
        throw new IoTPException("You don't have permission to perform this operation!",
            IoTPErrorCode.PERMISSION_DENIED);
      }
    }
    if (checkCustomerId && dashboard.getCustomerId() != null
        && !dashboard.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
      checkCustomerId(dashboard.getCustomerId());
    }
  }

  ComponentDescriptor checkComponentDescriptorByClazz(String clazz) throws IoTPException {
    try {
      log.debug("[{}] Lookup component descriptor", clazz);
      ComponentDescriptor componentDescriptor = checkNotNull(componentDescriptorService.getComponent(clazz));
      return componentDescriptor;
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  List<ComponentDescriptor> checkComponentDescriptorsByType(ComponentType type) throws IoTPException {
    try {
      log.debug("[{}] Lookup component descriptors", type);
      return componentDescriptorService.getComponents(type);
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  List<ComponentDescriptor> checkPluginActionsByPluginClazz(String pluginClazz) throws IoTPException {
    try {
      checkComponentDescriptorByClazz(pluginClazz);
      log.debug("[{}] Lookup plugin actions", pluginClazz);
      return componentDescriptorService.getPluginActions(pluginClazz);
    } catch (Exception e) {
      throw handleException(e, false);
    }
  }

  protected PluginMetaData checkPlugin(PluginMetaData plugin) throws IoTPException {
    checkNotNull(plugin);
    SecurityUser authUser = getCurrentUser();
    TenantId tenantId = plugin.getTenantId();
    validateId(tenantId, "Incorrect tenantId " + tenantId);
    if (authUser.getAuthority() != Authority.SYS_ADMIN) {
      if (authUser.getTenantId() == null
          || !tenantId.getId().equals(ModelConstants.NULL_UUID) && !authUser.getTenantId().equals(tenantId)) {
        throw new IoTPException("You don't have permission to perform this operation!",
            IoTPErrorCode.PERMISSION_DENIED);

      } else if (tenantId.getId().equals(ModelConstants.NULL_UUID)) {
        plugin.setConfiguration(null);
      }
    }
    return plugin;
  }

  protected PluginMetaData checkPlugin(PluginId pluginId) throws IoTPException {
    checkNotNull(pluginId);
    return checkPlugin(pluginService.findPluginById(pluginId));
  }

  protected RuleMetaData checkRule(RuleId ruleId) throws IoTPException {
    checkNotNull(ruleId);
    return checkRule(ruleService.findRuleById(ruleId));
  }

  protected RuleMetaData checkRule(RuleMetaData rule) throws IoTPException {
    checkNotNull(rule);
    SecurityUser authUser = getCurrentUser();
    TenantId tenantId = rule.getTenantId();
    validateId(tenantId, "Incorrect tenantId " + tenantId);
    if (authUser.getAuthority() != Authority.SYS_ADMIN) {
      if (authUser.getTenantId() == null
          || !tenantId.getId().equals(ModelConstants.NULL_UUID) && !authUser.getTenantId().equals(tenantId)) {
        throw new IoTPException("You don't have permission to perform this operation!",
            IoTPErrorCode.PERMISSION_DENIED);

      }
    }
    return rule;
  }

  protected String constructBaseUrl(HttpServletRequest request) {
    String scheme = request.getScheme();
    if (request.getHeader("x-forwarded-proto") != null) {
      scheme = request.getHeader("x-forwarded-proto");
    }
    int serverPort = request.getServerPort();
    if (request.getHeader("x-forwarded-port") != null) {
      try {
        serverPort = request.getIntHeader("x-forwarded-port");
      } catch (NumberFormatException e) {
      }
    }

    String baseUrl = String.format("%s://%s:%d", scheme, request.getServerName(), serverPort);
    return baseUrl;
  }
}

package org.iotp.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.iotp.infomgt.dao.device.DeviceSearchQuery;
import org.iotp.infomgt.dao.exception.IncorrectParameterException;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.Customer;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.DeviceType;
import org.iotp.infomgt.data.TenantDeviceType;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.security.DeviceCredentials;
import org.iotp.server.exception.IoTPException;
import org.iotp.server.msghub.ThingsMetaKafkaTopics;
import org.iotp.server.service.security.model.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;

@RestController
@RequestMapping("/api")
public class DeviceController extends BaseController {

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/device/{deviceId}", method = RequestMethod.GET)
  @ResponseBody
  public Device getDeviceById(@PathVariable("deviceId") String strDeviceId) throws IoTPException {
    checkParameter("deviceId", strDeviceId);
    try {
      DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
      return checkDeviceId(deviceId);
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/device", method = RequestMethod.POST)
  @ResponseBody
  public Device saveDevice(@RequestBody Device device) throws IoTPException {
    try {
      device.setTenantId(getCurrentUser().getTenantId());
      if (device.getType() == null) {
        device.setType("default");
      }
      Device savedDevice = checkNotNull(deviceService.saveDevice(device));
//      actorService.onDeviceNameOrTypeUpdate(savedDevice.getTenantId(), savedDevice.getId(), savedDevice.getName(),
//          savedDevice.getType());
      JsonObject json = new JsonObject();
      json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, savedDevice.getTenantId().toString());
      json.addProperty(ThingsMetaKafkaTopics.DEVICE_ID, savedDevice.getId().toString());
      json.addProperty(ThingsMetaKafkaTopics.DEVICE_NAME, savedDevice.getName().toString());
      json.addProperty(ThingsMetaKafkaTopics.DEVICE_TYPE, savedDevice.getType().toString());
      json.addProperty(ThingsMetaKafkaTopics.EVENT, ThingsMetaKafkaTopics.EVENT_DEVICENAMEORTYPE_UPDATE);
      msgProducer.send(ThingsMetaKafkaTopics.METADATA_DEVICE_TOPIC, savedDevice.getId().toString(), json.toString());

      return savedDevice;
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/device/{deviceId}", method = RequestMethod.DELETE)
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteDevice(@PathVariable("deviceId") String strDeviceId) throws IoTPException {
    checkParameter("deviceId", strDeviceId);
    try {
      DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
      checkDeviceId(deviceId);
      deviceService.deleteDevice(deviceId);
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/customer/{customerId}/device/{deviceId}", method = RequestMethod.POST)
  @ResponseBody
  public Device assignDeviceToCustomer(@PathVariable("customerId") String strCustomerId,
      @PathVariable("deviceId") String strDeviceId) throws IoTPException {
    checkParameter("customerId", strCustomerId);
    checkParameter("deviceId", strDeviceId);
    try {
      CustomerId customerId = new CustomerId(toUUID(strCustomerId));
      checkCustomerId(customerId);

      DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
      checkDeviceId(deviceId);

      return checkNotNull(deviceService.assignDeviceToCustomer(deviceId, customerId));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/customer/device/{deviceId}", method = RequestMethod.DELETE)
  @ResponseBody
  public Device unassignDeviceFromCustomer(@PathVariable("deviceId") String strDeviceId) throws IoTPException {
    checkParameter("deviceId", strDeviceId);
    try {
      DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
      Device device = checkDeviceId(deviceId);
      if (device.getCustomerId() == null || device.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
        throw new IncorrectParameterException("Device isn't assigned to any customer!");
      }
      return checkNotNull(deviceService.unassignDeviceFromCustomer(deviceId));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/customer/public/device/{deviceId}", method = RequestMethod.POST)
  @ResponseBody
  public Device assignDeviceToPublicCustomer(@PathVariable("deviceId") String strDeviceId) throws IoTPException {
    checkParameter("deviceId", strDeviceId);
    try {
      DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
      Device device = checkDeviceId(deviceId);
      Customer publicCustomer = customerService.findOrCreatePublicCustomer(device.getTenantId());
      return checkNotNull(deviceService.assignDeviceToCustomer(deviceId, publicCustomer.getId()));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/device/{deviceId}/credentials", method = RequestMethod.GET)
  @ResponseBody
  public DeviceCredentials getDeviceCredentialsByDeviceId(@PathVariable("deviceId") String strDeviceId)
      throws IoTPException {
    checkParameter("deviceId", strDeviceId);
    try {
      DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
      checkDeviceId(deviceId);
      return checkNotNull(deviceCredentialsService.findDeviceCredentialsByDeviceId(deviceId));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/device/credentials", method = RequestMethod.POST)
  @ResponseBody
  public DeviceCredentials saveDeviceCredentials(@RequestBody DeviceCredentials deviceCredentials)
      throws IoTPException {
    checkNotNull(deviceCredentials);
    try {
      checkDeviceId(deviceCredentials.getDeviceId());
      DeviceCredentials result = checkNotNull(deviceCredentialsService.updateDeviceCredentials(deviceCredentials));
      // actorService.onCredentialsUpdate(getCurrentUser().getTenantId(),
      // deviceCredentials.getDeviceId());
      JsonObject json = new JsonObject();
      json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, getCurrentUser().getTenantId().toString());
      json.addProperty(ThingsMetaKafkaTopics.DEVICE_ID, deviceCredentials.getDeviceId().toString());
      json.addProperty(ThingsMetaKafkaTopics.EVENT, ThingsMetaKafkaTopics.EVENT_CREDENTIALS_UPDATE);
      msgProducer.send(ThingsMetaKafkaTopics.METADATA_DEVICE_TOPIC, deviceCredentials.getDeviceId().toString(),
          json.toString());

      return result;
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/tenant/devices", params = { "limit" }, method = RequestMethod.GET)
  @ResponseBody
  public TextPageData<Device> getTenantDevices(@RequestParam int limit, @RequestParam(required = false) String type,
      @RequestParam(required = false) String textSearch, @RequestParam(required = false) String idOffset,
      @RequestParam(required = false) String textOffset) throws IoTPException {
    try {
      TenantId tenantId = getCurrentUser().getTenantId();
      TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
      if (type != null && type.trim().length() > 0) {
        return checkNotNull(deviceService.findDevicesByTenantIdAndType(tenantId, type, pageLink));
      } else {
        return checkNotNull(deviceService.findDevicesByTenantId(tenantId, pageLink));
      }
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/tenant/devices", params = { "deviceName" }, method = RequestMethod.GET)
  @ResponseBody
  public Device getTenantDevice(@RequestParam String deviceName) throws IoTPException {
    try {
      TenantId tenantId = getCurrentUser().getTenantId();
      return checkNotNull(deviceService.findDeviceByTenantIdAndName(tenantId, deviceName));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/customer/{customerId}/devices", params = { "limit" }, method = RequestMethod.GET)
  @ResponseBody
  public TextPageData<Device> getCustomerDevices(@PathVariable("customerId") String strCustomerId,
      @RequestParam int limit, @RequestParam(required = false) String type,
      @RequestParam(required = false) String textSearch, @RequestParam(required = false) String idOffset,
      @RequestParam(required = false) String textOffset) throws IoTPException {
    checkParameter("customerId", strCustomerId);
    try {
      TenantId tenantId = getCurrentUser().getTenantId();
      CustomerId customerId = new CustomerId(toUUID(strCustomerId));
      checkCustomerId(customerId);
      TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
      if (type != null && type.trim().length() > 0) {
        return checkNotNull(
            deviceService.findDevicesByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
      } else {
        return checkNotNull(deviceService.findDevicesByTenantIdAndCustomerId(tenantId, customerId, pageLink));
      }
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/devices", params = { "deviceIds" }, method = RequestMethod.GET)
  @ResponseBody
  public List<Device> getDevicesByIds(@RequestParam("deviceIds") String[] strDeviceIds) throws IoTPException {
    checkArrayParameter("deviceIds", strDeviceIds);
    try {
      SecurityUser user = getCurrentUser();
      TenantId tenantId = user.getTenantId();
      CustomerId customerId = user.getCustomerId();
      List<DeviceId> deviceIds = new ArrayList<>();
      for (String strDeviceId : strDeviceIds) {
        deviceIds.add(new DeviceId(toUUID(strDeviceId)));
      }
      ListenableFuture<List<Device>> devices;
      if (customerId == null || customerId.isNullUid()) {
        devices = deviceService.findDevicesByTenantIdAndIdsAsync(tenantId, deviceIds);
      } else {
        devices = deviceService.findDevicesByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, deviceIds);
      }
      return checkNotNull(devices.get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/devices", method = RequestMethod.POST)
  @ResponseBody
  public List<Device> findByQuery(@RequestBody DeviceSearchQuery query) throws IoTPException {
    checkNotNull(query);
    checkNotNull(query.getParameters());
    checkNotNull(query.getDeviceTypes());
    checkEntityId(query.getParameters().getEntityId());
    try {
      List<Device> devices = checkNotNull(deviceService.findDevicesByQuery(query).get());
      devices = devices.stream().filter(device -> {
        try {
          checkDevice(device);
          return true;
        } catch (IoTPException e) {
          return false;
        }
      }).collect(Collectors.toList());
      return devices;
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/device/types", method = RequestMethod.GET)
  @ResponseBody
  public List<TenantDeviceType> getDeviceTypes() throws IoTPException {
    try {
      SecurityUser user = getCurrentUser();
      TenantId tenantId = user.getTenantId();
      int limit = 1000; // by default
      TextPageLink pageLink = createPageLink(limit, null, null, null);
      TextPageData<DeviceType> findDevicesByTenantId = deviceTypeService.findDevicesByTenantId(tenantId, pageLink);
      List<TenantDeviceType> result = new ArrayList<TenantDeviceType>();
      if (findDevicesByTenantId != null) {
        List<DeviceType> deviceTypes = findDevicesByTenantId.getData();
        if (deviceTypes != null) {
          for (DeviceType deviceType : deviceTypes) {
            TenantDeviceType t = new TenantDeviceType();
            t.setTenantId(tenantId);
            t.setType(deviceType.getName());
            result.add(t);
          }
        }
      }
      return result;
      // ListenableFuture<List<TenantDeviceType>> deviceTypes =
      // deviceService.findDeviceTypesByTenantId(tenantId);
      // return checkNotNull(deviceTypes.get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

}

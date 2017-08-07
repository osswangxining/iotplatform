package org.iotp.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.iotp.infomgt.dao.asset.AssetSearchQuery;
import org.iotp.infomgt.dao.exception.IncorrectParameterException;
import org.iotp.infomgt.dao.model.ModelConstants;
import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.Customer;
import org.iotp.infomgt.data.TenantAssetType;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.security.AssetCredentials;
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
public class AssetController extends BaseController {

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/asset/{assetId}", method = RequestMethod.GET)
  @ResponseBody
  public Asset getAssetById(@PathVariable("assetId") String strAssetId) throws IoTPException {
    checkParameter("assetId", strAssetId);
    try {
      AssetId assetId = new AssetId(toUUID(strAssetId));
      return checkAssetId(assetId);
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/asset", method = RequestMethod.POST)
  @ResponseBody
  public Asset saveAsset(@RequestBody Asset asset) throws IoTPException {
    try {
      asset.setTenantId(getCurrentUser().getTenantId());
      return checkNotNull(assetService.saveAsset(asset));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/asset/{assetId}", method = RequestMethod.DELETE)
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteAsset(@PathVariable("assetId") String strAssetId) throws IoTPException {
    checkParameter("assetId", strAssetId);
    try {
      AssetId assetId = new AssetId(toUUID(strAssetId));
      checkAssetId(assetId);
      assetService.deleteAsset(assetId);
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/customer/{customerId}/asset/{assetId}", method = RequestMethod.POST)
  @ResponseBody
  public Asset assignAssetToCustomer(@PathVariable("customerId") String strCustomerId,
      @PathVariable("assetId") String strAssetId) throws IoTPException {
    checkParameter("customerId", strCustomerId);
    checkParameter("assetId", strAssetId);
    try {
      CustomerId customerId = new CustomerId(toUUID(strCustomerId));
      checkCustomerId(customerId);

      AssetId assetId = new AssetId(toUUID(strAssetId));
      checkAssetId(assetId);

      return checkNotNull(assetService.assignAssetToCustomer(assetId, customerId));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/customer/asset/{assetId}", method = RequestMethod.DELETE)
  @ResponseBody
  public Asset unassignAssetFromCustomer(@PathVariable("assetId") String strAssetId) throws IoTPException {
    checkParameter("assetId", strAssetId);
    try {
      AssetId assetId = new AssetId(toUUID(strAssetId));
      Asset asset = checkAssetId(assetId);
      if (asset.getCustomerId() == null || asset.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
        throw new IncorrectParameterException("Asset isn't assigned to any customer!");
      }
      return checkNotNull(assetService.unassignAssetFromCustomer(assetId));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/customer/public/asset/{assetId}", method = RequestMethod.POST)
  @ResponseBody
  public Asset assignAssetToPublicCustomer(@PathVariable("assetId") String strAssetId) throws IoTPException {
    checkParameter("assetId", strAssetId);
    try {
      AssetId assetId = new AssetId(toUUID(strAssetId));
      Asset asset = checkAssetId(assetId);
      Customer publicCustomer = customerService.findOrCreatePublicCustomer(asset.getTenantId());
      return checkNotNull(assetService.assignAssetToCustomer(assetId, publicCustomer.getId()));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/asset/{assetId}/credentials", method = RequestMethod.GET)
  @ResponseBody
  public AssetCredentials getAssetCredentialsByAssetId(@PathVariable("assetId") String strAssetId)
      throws IoTPException {
    checkParameter("assetId", strAssetId);
    try {
      AssetId assetId = new AssetId(toUUID(strAssetId));
      checkAssetId(assetId);
      return checkNotNull(assetCredentialsService.findAssetCredentialsByAssetId(assetId));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/asset/credentials", method = RequestMethod.POST)
  @ResponseBody
  public AssetCredentials saveAssetCredentials(@RequestBody AssetCredentials assetCredentials) throws IoTPException {
    checkNotNull(assetCredentials);
    try {
      checkAssetId(assetCredentials.getAssetId());
      AssetCredentials result = checkNotNull(assetCredentialsService.updateAssetCredentials(assetCredentials));
      // actorService.onCredentialsUpdate(getCurrentUser().getTenantId(),
      // assetCredentials.getAssetId());
      JsonObject json = new JsonObject();
      json.addProperty(ThingsMetaKafkaTopics.TENANT_ID, getCurrentUser().getTenantId().toString());
      json.addProperty(ThingsMetaKafkaTopics.ASSET_ID, assetCredentials.getAssetId().toString());
      json.addProperty(ThingsMetaKafkaTopics.EVENT, ThingsMetaKafkaTopics.EVENT_CREDENTIALS_UPDATE);
      msgProducer.send(ThingsMetaKafkaTopics.METADATA_ASSET_TOPIC, assetCredentials.getAssetId().toString(), json.toString());
      return result;
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/tenant/assets", params = { "limit" }, method = RequestMethod.GET)
  @ResponseBody
  public TextPageData<Asset> getTenantAssets(@RequestParam int limit, @RequestParam(required = false) String type,
      @RequestParam(required = false) String textSearch, @RequestParam(required = false) String idOffset,
      @RequestParam(required = false) String textOffset) throws IoTPException {
    try {
      TenantId tenantId = getCurrentUser().getTenantId();
      TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
      if (type != null && type.trim().length() > 0) {
        return checkNotNull(assetService.findAssetsByTenantIdAndType(tenantId, type, pageLink));
      } else {
        return checkNotNull(assetService.findAssetsByTenantId(tenantId, pageLink));
      }
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAuthority('TENANT_ADMIN')")
  @RequestMapping(value = "/tenant/assets", params = { "assetName" }, method = RequestMethod.GET)
  @ResponseBody
  public Asset getTenantAsset(@RequestParam String assetName) throws IoTPException {
    try {
      TenantId tenantId = getCurrentUser().getTenantId();
      return checkNotNull(assetService.findAssetByTenantIdAndName(tenantId, assetName));
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/customer/{customerId}/assets", params = { "limit" }, method = RequestMethod.GET)
  @ResponseBody
  public TextPageData<Asset> getCustomerAssets(@PathVariable("customerId") String strCustomerId,
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
            assetService.findAssetsByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
      } else {
        return checkNotNull(assetService.findAssetsByTenantIdAndCustomerId(tenantId, customerId, pageLink));
      }
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/assets", params = { "assetIds" }, method = RequestMethod.GET)
  @ResponseBody
  public List<Asset> getAssetsByIds(@RequestParam("assetIds") String[] strAssetIds) throws IoTPException {
    checkArrayParameter("assetIds", strAssetIds);
    try {
      SecurityUser user = getCurrentUser();
      TenantId tenantId = user.getTenantId();
      CustomerId customerId = user.getCustomerId();
      List<AssetId> assetIds = new ArrayList<>();
      for (String strAssetId : strAssetIds) {
        assetIds.add(new AssetId(toUUID(strAssetId)));
      }
      ListenableFuture<List<Asset>> assets;
      if (customerId == null || customerId.isNullUid()) {
        assets = assetService.findAssetsByTenantIdAndIdsAsync(tenantId, assetIds);
      } else {
        assets = assetService.findAssetsByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, assetIds);
      }
      return checkNotNull(assets.get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/assets", method = RequestMethod.POST)
  @ResponseBody
  public List<Asset> findByQuery(@RequestBody AssetSearchQuery query) throws IoTPException {
    checkNotNull(query);
    checkNotNull(query.getParameters());
    checkNotNull(query.getAssetTypes());
    checkEntityId(query.getParameters().getEntityId());
    try {
      List<Asset> assets = checkNotNull(assetService.findAssetsByQuery(query).get());
      assets = assets.stream().filter(asset -> {
        try {
          checkAsset(asset);
          return true;
        } catch (IoTPException e) {
          return false;
        }
      }).collect(Collectors.toList());
      return assets;
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/asset/types", method = RequestMethod.GET)
  @ResponseBody
  public List<TenantAssetType> getAssetTypes() throws IoTPException {
    try {
      SecurityUser user = getCurrentUser();
      TenantId tenantId = user.getTenantId();
      ListenableFuture<List<TenantAssetType>> assetTypes = assetService.findAssetTypesByTenantId(tenantId);
      return checkNotNull(assetTypes.get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }
}

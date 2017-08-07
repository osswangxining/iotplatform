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

import static org.iotp.infomgt.dao.util.Validator.validateId;
import static org.iotp.infomgt.dao.util.Validator.validateString;

import org.iotp.infomgt.dao.exception.DataValidationException;
import org.iotp.infomgt.dao.util.DataValidator;
import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.common.CacheConstants;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.security.AssetCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AssetCredentialsServiceImpl implements AssetCredentialsService {

  @Autowired
  private AssetCredentialsDao assetCredentialsDao;

  @Autowired
  private AssetService assetService;

  @Override
  public AssetCredentials findAssetCredentialsByAssetId(AssetId assetId) {
    log.trace("Executing findAssetCredentialsByAssetId [{}]", assetId);
    validateId(assetId, "Incorrect assetId " + assetId);
    return assetCredentialsDao.findByAssetId(assetId.getId());
  }

  @Override
  @Cacheable(cacheNames = CacheConstants.ASSET_CREDENTIALS_CACHE, unless = "#result == null")
  public AssetCredentials findAssetCredentialsByCredentialsId(String credentialsId) {
    log.trace("Executing findAssetCredentialsByCredentialsId [{}]", credentialsId);
    validateString(credentialsId, "Incorrect credentialsId " + credentialsId);
    return assetCredentialsDao.findByCredentialsId(credentialsId);
  }

  @Override
  @CacheEvict(cacheNames = CacheConstants.ASSET_CREDENTIALS_CACHE, keyGenerator = "previousAssetCredentialsId", beforeInvocation = true)
  public AssetCredentials updateAssetCredentials(AssetCredentials assetCredentials) {
    return saveOrUpdare(assetCredentials);
  }

  @Override
  public AssetCredentials createAssetCredentials(AssetCredentials assetCredentials) {
    return saveOrUpdare(assetCredentials);
  }

  private AssetCredentials saveOrUpdare(AssetCredentials assetCredentials) {
    // if (assetCredentials.getCredentialsType() ==
    // AssetCredentialsType.X509_CERTIFICATE) {
    // formatCertData(assetCredentials);
    // }
    log.trace("Executing updateAssetCredentials [{}]", assetCredentials);
    credentialsValidator.validate(assetCredentials);
    return assetCredentialsDao.save(assetCredentials);
  }

  // private void formatCertData(AssetCredentials assetCredentials) {
  // String cert =
  // EncryptionUtil.trimNewLines(assetCredentials.getCredentialsValue());
  // String sha3Hash = EncryptionUtil.getSha3Hash(cert);
  // assetCredentials.setCredentialsId(sha3Hash);
  // assetCredentials.setCredentialsValue(cert);
  // }

  @Override
  @CacheEvict(cacheNames = CacheConstants.ASSET_CREDENTIALS_CACHE, key = "#assetCredentials.credentialsId")
  public void deleteAssetCredentials(AssetCredentials assetCredentials) {
    log.trace("Executing deleteAssetCredentials [{}]", assetCredentials);
    assetCredentialsDao.removeById(assetCredentials.getUuidId());
  }

  private DataValidator<AssetCredentials> credentialsValidator = new DataValidator<AssetCredentials>() {

    @Override
    protected void validateCreate(AssetCredentials assetCredentials) {
      AssetCredentials existingCredentialsEntity = assetCredentialsDao
          .findByCredentialsId(assetCredentials.getCredentialsId());
      if (existingCredentialsEntity != null) {
        throw new DataValidationException("Create of existent asset credentials!");
      }
    }

    @Override
    protected void validateUpdate(AssetCredentials assetCredentials) {
      AssetCredentials existingCredentials = assetCredentialsDao.findById(assetCredentials.getUuidId());
      if (existingCredentials == null) {
        throw new DataValidationException("Unable to update non-existent asset credentials!");
      }
      AssetCredentials sameCredentialsId = assetCredentialsDao.findByCredentialsId(assetCredentials.getCredentialsId());
      if (sameCredentialsId != null && !sameCredentialsId.getUuidId().equals(assetCredentials.getUuidId())) {
        throw new DataValidationException("Specified credentials are already registered!");
      }
    }

    @Override
    protected void validateDataImpl(AssetCredentials assetCredentials) {
      if (assetCredentials.getAssetId() == null) {
        throw new DataValidationException("Asset credentials should be assigned to asset!");
      }
      if (assetCredentials.getCredentialsType() == null) {
        throw new DataValidationException("Asset credentials type should be specified!");
      }
      if (StringUtils.isEmpty(assetCredentials.getCredentialsId())) {
        throw new DataValidationException("Asset credentials id should be specified!");
      }
      switch (assetCredentials.getCredentialsType()) {
      case ACCESS_TOKEN:
        if (assetCredentials.getCredentialsId().length() < 1 || assetCredentials.getCredentialsId().length() > 20) {
          throw new DataValidationException(
              "Incorrect access token length [" + assetCredentials.getCredentialsId().length() + "]!");
        }
        break;
      // case X509_CERTIFICATE:
      // if (assetCredentials.getCredentialsId().length() == 0) {
      // throw new DataValidationException("X509 Certificate Cannot be empty!");
      // }
      default:
        break;
      }
      Asset asset = assetService.findAssetById(assetCredentials.getAssetId());
      if (asset == null) {
        throw new DataValidationException("Can't assign asset credentials to non-existent asset!");
      }
    }
  };

}

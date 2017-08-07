package org.iotp.iothub.server.security;

import java.util.Optional;

import org.iotp.infomgt.data.Asset;
import org.iotp.infomgt.data.id.AssetId;
import org.iotp.infomgt.data.security.AssetCredentialsFilter;

public interface AssetAuthService {

  AssetAuthResult process(AssetCredentialsFilter credentials);

  Optional<Asset> findAssetById(AssetId assetId);

}

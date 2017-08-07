package org.iotp.analytics.ruleengine.api.asset;

import org.iotp.analytics.ruleengine.api.device.DeviceAttributes;
import org.iotp.infomgt.data.id.AssetId;

import lombok.Data;

/**
 * Contains basic device metadata;
 *
 */
@Data
public final class AssetMetaData {

  final AssetId assetId;
  final String assetName;
  final String assetType;
  final DeviceAttributes assetAttributes;

}

package org.iotp.analytics.ruleengine.common.msg.kv;

import java.io.Serializable;
import java.util.List;

import org.iotp.infomgt.data.kv.AttributeKey;
import org.iotp.infomgt.data.kv.AttributeKvEntry;

public interface AttributesKVMsg extends Serializable {

  List<AttributeKvEntry> getClientAttributes();

  List<AttributeKvEntry> getSharedAttributes();

  List<AttributeKey> getDeletedAttributes();
}

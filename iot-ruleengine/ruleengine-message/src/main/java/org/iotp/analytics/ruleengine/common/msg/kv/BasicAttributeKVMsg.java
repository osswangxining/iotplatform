package org.iotp.analytics.ruleengine.common.msg.kv;

import java.util.Collections;
import java.util.List;

import org.iotp.infomgt.data.kv.AttributeKey;
import org.iotp.infomgt.data.kv.AttributeKvEntry;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicAttributeKVMsg implements AttributesKVMsg {

  private static final long serialVersionUID = 1L;

  private final List<AttributeKvEntry> clientAttributes;
  private final List<AttributeKvEntry> sharedAttributes;
  private final List<AttributeKey> deletedAttributes;

  public static BasicAttributeKVMsg fromClient(List<AttributeKvEntry> attributes) {
    return new BasicAttributeKVMsg(attributes, Collections.emptyList(), Collections.emptyList());
  }

  public static BasicAttributeKVMsg fromShared(List<AttributeKvEntry> attributes) {
    return new BasicAttributeKVMsg(Collections.emptyList(), attributes, Collections.emptyList());
  }

  public static BasicAttributeKVMsg from(List<AttributeKvEntry> client, List<AttributeKvEntry> shared) {
    return new BasicAttributeKVMsg(client, shared, Collections.emptyList());
  }

  public static AttributesKVMsg fromDeleted(List<AttributeKey> shared) {
    return new BasicAttributeKVMsg(Collections.emptyList(), Collections.emptyList(), shared);
  }
}

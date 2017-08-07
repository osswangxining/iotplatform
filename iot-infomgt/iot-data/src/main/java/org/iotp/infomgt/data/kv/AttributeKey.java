package org.iotp.infomgt.data.kv;

import java.io.Serializable;

import lombok.Data;

@Data
public class AttributeKey implements Serializable {
    /**
   * 
   */
  private static final long serialVersionUID = -4366789184110844957L;
    private final String scope;
    private final String attributeKey;
}

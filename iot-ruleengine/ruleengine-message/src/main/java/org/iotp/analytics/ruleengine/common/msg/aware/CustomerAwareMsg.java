package org.iotp.analytics.ruleengine.common.msg.aware;

import org.iotp.infomgt.data.id.CustomerId;

public interface CustomerAwareMsg {

  CustomerId getCustomerId();

}

package org.iotp.analytics.ruleengine.common.msg.aware;

import org.iotp.infomgt.data.id.TenantId;

public interface TenantAwareMsg {

	TenantId getTenantId();
	
}

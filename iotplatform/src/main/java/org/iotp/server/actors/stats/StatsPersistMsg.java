package org.iotp.server.actors.stats;

import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.TenantId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public final class StatsPersistMsg {
  private long messagesProcessed;
  private long errorsOccurred;
  private TenantId tenantId;
  private EntityId entityId;
}

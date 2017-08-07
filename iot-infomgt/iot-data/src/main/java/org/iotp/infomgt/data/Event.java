package org.iotp.infomgt.data;

import org.iotp.infomgt.data.common.BaseData;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.EventId;
import org.iotp.infomgt.data.id.TenantId;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class Event extends BaseData<EventId> {

  /**
   * 
   */
  private static final long serialVersionUID = -3747202219416738340L;
  private TenantId tenantId;
  private String type;
  private String uid;
  private EntityId entityId;
  private JsonNode body;

  public Event() {
    super();
  }

  public Event(EventId id) {
    super(id);
  }

  public Event(Event event) {
    super(event);
  }

}

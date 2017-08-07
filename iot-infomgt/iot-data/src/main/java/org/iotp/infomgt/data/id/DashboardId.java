package org.iotp.infomgt.data.id;

import java.util.UUID;

import org.iotp.infomgt.data.common.ThingType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardId extends UUIDBased implements EntityId {

  /**
   * 
   */
  private static final long serialVersionUID = -3147182434702212610L;

  @JsonCreator
  public DashboardId(@JsonProperty("id") UUID id) {
    super(id);
  }

  public static DashboardId fromString(String dashboardId) {
    return new DashboardId(UUID.fromString(dashboardId));
  }

  @JsonIgnore
  @Override
  public ThingType getEntityType() {
    return ThingType.DASHBOARD;
  }
}

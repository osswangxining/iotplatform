package org.iotp.infomgt.data.alarm;

import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.page.TimePageLink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AlarmQuery {

  private EntityId affectedEntityId;
  private TimePageLink pageLink;
  private AlarmSearchStatus searchStatus;
  private AlarmStatus status;
  private Boolean fetchOriginator;

}

package org.iotp.infomgt.data.page;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
public abstract class BasePageLink implements Serializable {

  private static final long serialVersionUID = -4189954843653250481L;

  @Getter
  protected final int limit;

  @Getter
  @Setter
  protected UUID idOffset;

}

package org.iotp.server.actors.service;

import org.iotp.server.actors.ActorSystemContext;

import akka.japi.Creator;

public abstract class ContextBasedCreator<T> implements Creator<T> {

  private static final long serialVersionUID = 1L;

  protected final ActorSystemContext context;

  public ContextBasedCreator(ActorSystemContext context) {
    super();
    this.context = context;
  }
}

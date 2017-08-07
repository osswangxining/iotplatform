package org.iotp.server.actors.rule;

public class RulesProcessedMsg {
  private final ChainProcessingContext ctx;

  public RulesProcessedMsg(ChainProcessingContext ctx) {
    super();
    this.ctx = ctx;
  }

  public ChainProcessingContext getCtx() {
    return ctx;
  }

  @Override
  public String toString() {
    return "RulesProcessedMsg [ctx=" + ctx + "]";
  }
}

package org.iotp.server.actors.rule;

public interface RuleActorChain {

  int size();

  RuleActorMetaData getRuleActorMd(int index);

}

package org.iotp.iothub.server.service.environment;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.Environment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 */

@Service("environmentLogService")
@ConditionalOnProperty(prefix = "zk", value = "enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class EnvironmentLogService {

  @PostConstruct
  public void init() {
    Environment.logEnv("environment: ", log);
  }

}

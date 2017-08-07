package org.iotp.iothub.server;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootConfiguration
@EnableAsync
@ComponentScan({ "org.iotp" })
public class IoTHubServerApplication {

  private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
  private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "iothub";

  public static void main(String[] args) {
    SpringApplication.run(IoTHubServerApplication.class, updateArguments(args));
  }

  private static String[] updateArguments(String[] args) {
   
    if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
      String[] modifiedArgs = new String[args.length + 1];
      System.arraycopy(args, 0, modifiedArgs, 0, args.length);
      modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
      return modifiedArgs;
    }
    
    return args;
  }
  
  
}

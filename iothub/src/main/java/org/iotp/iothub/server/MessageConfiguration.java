package org.iotp.iothub.server;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageConfiguration {

  @Bean
  @Primary
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("i18n/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  // @Bean
  // public EmbeddedServletContainerFactory servletContainer() {
  // TomcatEmbeddedServletContainerFactory factory =
  // new TomcatEmbeddedServletContainerFactory();
  // return factory;
  // }
}

package org.iotp.analytics.ruleengine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.iotp.infomgt.data.plugin.ComponentScope;

/**
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Processor {

  String name();

  ComponentScope scope() default ComponentScope.TENANT;

  String descriptor() default "EmptyJsonDescriptor.json";

  Class<?> configuration() default EmptyComponentConfiguration.class;

}

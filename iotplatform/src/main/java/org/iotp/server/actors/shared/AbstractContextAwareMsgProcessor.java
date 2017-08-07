package org.iotp.server.actors.shared;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.iotp.analytics.ruleengine.annotation.Action;
import org.iotp.analytics.ruleengine.annotation.ConfigurableComponent;
import org.iotp.analytics.ruleengine.annotation.Filter;
import org.iotp.analytics.ruleengine.annotation.Plugin;
import org.iotp.analytics.ruleengine.annotation.Processor;
import org.iotp.infomgt.data.plugin.ComponentDescriptor;
import org.iotp.infomgt.data.plugin.ComponentType;
import org.iotp.server.actors.ActorSystemContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Scheduler;
import akka.event.LoggingAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

public abstract class AbstractContextAwareMsgProcessor {

  protected final ActorSystemContext systemContext;
  protected final LoggingAdapter logger;
  protected final ObjectMapper mapper = new ObjectMapper();

  protected AbstractContextAwareMsgProcessor(ActorSystemContext systemContext, LoggingAdapter logger) {
    super();
    this.systemContext = systemContext;
    this.logger = logger;
  }

  protected ActorRef getAppActor() {
    return systemContext.getAppActor();
  }

  protected Scheduler getScheduler() {
    return systemContext.getScheduler();
  }

  protected ExecutionContextExecutor getSystemDispatcher() {
    return systemContext.getActorSystem().dispatcher();
  }

  protected void schedulePeriodicMsgWithDelay(ActorContext ctx, Object msg, long delayInMs, long periodInMs) {
    schedulePeriodicMsgWithDelay(ctx, msg, delayInMs, periodInMs, ctx.self());
  }

  protected void schedulePeriodicMsgWithDelay(ActorContext ctx, Object msg, long delayInMs, long periodInMs,
      ActorRef target) {
    logger.debug("Scheduling periodic msg {} every {} ms with delay {} ms", msg, periodInMs, delayInMs);
    getScheduler().schedule(Duration.create(delayInMs, TimeUnit.MILLISECONDS),
        Duration.create(periodInMs, TimeUnit.MILLISECONDS), target, msg, getSystemDispatcher(), null);
  }

  protected void scheduleMsgWithDelay(ActorContext ctx, Object msg, long delayInMs) {
    scheduleMsgWithDelay(ctx, msg, delayInMs, ctx.self());
  }

  protected void scheduleMsgWithDelay(ActorContext ctx, Object msg, long delayInMs, ActorRef target) {
    logger.debug("Scheduling msg {} with delay {} ms", msg, delayInMs);
    getScheduler().scheduleOnce(Duration.create(delayInMs, TimeUnit.MILLISECONDS), target, msg, getSystemDispatcher(),
        null);
  }

  protected <T extends ConfigurableComponent> T initComponent(JsonNode componentNode) throws Exception {
    ComponentConfiguration configuration = new ComponentConfiguration(componentNode.get("clazz").asText(),
        componentNode.get("name").asText(), mapper.writeValueAsString(componentNode.get("configuration")));
    logger.info("Initializing [{}][{}] component", configuration.getName(), configuration.getClazz());
    ComponentDescriptor componentDescriptor = systemContext.getComponentService().getComponent(configuration.getClazz())
        .orElseThrow(() -> new InstantiationException("Component Not found!"));
    return initComponent(componentDescriptor, configuration);
  }

  protected <T extends ConfigurableComponent> T initComponent(ComponentDescriptor componentDefinition,
      ComponentConfiguration configuration) throws Exception {
    return initComponent(componentDefinition.getClazz(), componentDefinition.getType(),
        configuration.getConfiguration());
  }

  protected <T extends ConfigurableComponent> T initComponent(String clazz, ComponentType type, String configuration)
      throws Exception {
    Class<?> componentClazz = Class.forName(clazz);
    T component = (T) (componentClazz.newInstance());
    Class<?> configurationClazz;
    switch (type) {
    case FILTER:
      configurationClazz = ((Filter) componentClazz.getAnnotation(Filter.class)).configuration();
      break;
    case PROCESSOR:
      configurationClazz = ((Processor) componentClazz.getAnnotation(Processor.class)).configuration();
      break;
    case ACTION:
      configurationClazz = ((Action) componentClazz.getAnnotation(Action.class)).configuration();
      break;
    case PLUGIN:
      configurationClazz = ((Plugin) componentClazz.getAnnotation(Plugin.class)).configuration();
      break;
    default:
      throw new IllegalStateException("Component with type: " + type + " is not supported!");
    }
    component.init(decode(configuration, configurationClazz));
    return component;
  }

  public <C> C decode(String configuration, Class<C> configurationClazz) throws IOException, RuntimeException {
    logger.info("Initializing using configuration: {}", configuration);
    return mapper.readValue(configuration, configurationClazz);
  }

  @Data
  @AllArgsConstructor
  private static class ComponentConfiguration {
    private final String clazz;
    private final String name;
    private final String configuration;
  }

}

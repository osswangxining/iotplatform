package org.iotp.analytics.ruleengine.core.processor;

import java.util.Optional;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.parser.ParseException;
import org.iotp.analytics.ruleengine.annotation.Processor;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.api.rules.RuleProcessingMetaData;
import org.iotp.analytics.ruleengine.api.rules.RuleProcessor;
import org.iotp.analytics.ruleengine.api.rules.SimpleRuleLifecycleComponent;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.core.utils.VelocityUtils;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.common.DataConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Processor(name = "(Deprecated) Alarm Deduplication Processor", descriptor = "AlarmDeduplicationProcessorDescriptor.json", configuration = AlarmDeduplicationProcessorConfiguration.class)
@Slf4j
public class AlarmDeduplicationProcessor extends SimpleRuleLifecycleComponent
    implements RuleProcessor<AlarmDeduplicationProcessorConfiguration> {

  public static final String IS_NEW_ALARM = "isNewAlarm";
  private ObjectMapper mapper = new ObjectMapper();
  private AlarmDeduplicationProcessorConfiguration configuration;
  private Template alarmIdTemplate;
  private Template alarmBodyTemplate;

  @Override
  public void init(AlarmDeduplicationProcessorConfiguration configuration) {
    this.configuration = configuration;
    try {
      this.alarmIdTemplate = VelocityUtils.create(configuration.getAlarmIdTemplate(), "Alarm Id Template");
      this.alarmBodyTemplate = VelocityUtils.create(configuration.getAlarmBodyTemplate(), "Alarm Body Template");
    } catch (ParseException e) {
      log.error("Failed to create templates based on provided configuration!", e);
      throw new RuntimeException("Failed to create templates based on provided configuration!", e);
    }
  }

  @Override
  public RuleProcessingMetaData process(RuleContext ctx, ToDeviceActorMsg msg) throws RuleException {
    RuleProcessingMetaData md = new RuleProcessingMetaData();
    VelocityContext context = VelocityUtils.createContext(ctx.getDeviceMetaData(), msg.getPayload());
    String alarmId = VelocityUtils.merge(alarmIdTemplate, context);
    String alarmBody = VelocityUtils.merge(alarmBodyTemplate, context);
    Optional<Event> existingEvent = ctx.findEvent(DataConstants.ALARM, alarmId);
    if (!existingEvent.isPresent()) {
      Event event = new Event();
      event.setType(DataConstants.ALARM);
      event.setUid(alarmId);
      event.setBody(mapper.createObjectNode().put("body", alarmBody));
      Optional<Event> savedEvent = ctx.saveIfNotExists(event);
      if (savedEvent.isPresent()) {
        log.info("New Alarm detected: '{}'", alarmId);
        md.put(IS_NEW_ALARM, Boolean.TRUE);
        md.put("alarmId", alarmId);
        md.put("alarmBody", alarmBody);
        for (Object key : context.getKeys()) {
          md.put(key.toString(), context.get(key.toString()));
        }
      }
    }
    return md;
  }
}

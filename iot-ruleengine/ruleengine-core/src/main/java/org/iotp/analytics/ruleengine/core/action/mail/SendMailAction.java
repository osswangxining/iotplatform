package org.iotp.analytics.ruleengine.core.action.mail;

import java.util.Optional;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.parser.ParseException;
import org.iotp.analytics.ruleengine.annotation.Action;
import org.iotp.analytics.ruleengine.api.plugins.PluginAction;
import org.iotp.analytics.ruleengine.api.rules.RuleContext;
import org.iotp.analytics.ruleengine.api.rules.RuleProcessingMetaData;
import org.iotp.analytics.ruleengine.api.rules.SimpleRuleLifecycleComponent;
import org.iotp.analytics.ruleengine.common.msg.device.ToDeviceActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.ToDeviceMsg;
import org.iotp.analytics.ruleengine.core.utils.VelocityUtils;
import org.iotp.analytics.ruleengine.plugins.msg.PluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.ResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Action(name = "Send Mail Action", descriptor = "SendMailActionDescriptor.json", configuration = SendMailActionConfiguration.class)
@Slf4j
public class SendMailAction extends SimpleRuleLifecycleComponent implements PluginAction<SendMailActionConfiguration> {

  private SendMailActionConfiguration configuration;
  private Optional<Template> fromTemplate;
  private Optional<Template> toTemplate;
  private Optional<Template> ccTemplate;
  private Optional<Template> bccTemplate;
  private Optional<Template> subjectTemplate;
  private Optional<Template> bodyTemplate;

  @Override
  public void init(SendMailActionConfiguration configuration) {
    this.configuration = configuration;
    try {
      fromTemplate = toTemplate(configuration.getFromTemplate(), "From Template");
      toTemplate = toTemplate(configuration.getToTemplate(), "To Template");
      ccTemplate = toTemplate(configuration.getCcTemplate(), "Cc Template");
      bccTemplate = toTemplate(configuration.getBccTemplate(), "Bcc Template");
      subjectTemplate = toTemplate(configuration.getSubjectTemplate(), "Subject Template");
      bodyTemplate = toTemplate(configuration.getBodyTemplate(), "Body Template");
    } catch (ParseException e) {
      log.error("Failed to create templates based on provided configuration!", e);
      throw new RuntimeException("Failed to create templates based on provided configuration!", e);
    }
  }

  private Optional<Template> toTemplate(String source, String name) throws ParseException {
    if (!StringUtils.isEmpty(source)) {
      return Optional.of(VelocityUtils.create(source, name));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<RuleToPluginMsg<?>> convert(RuleContext ctx, ToDeviceActorMsg toDeviceActorMsg,
      RuleProcessingMetaData metadata) {
    String sendFlag = configuration.getSendFlag();
    if (StringUtils.isEmpty(sendFlag) || (Boolean) metadata.get(sendFlag).orElse(Boolean.FALSE)) {
      VelocityContext context = VelocityUtils.createContext(metadata);

      SendMailActionMsg.SendMailActionMsgBuilder builder = SendMailActionMsg.builder();
      fromTemplate.ifPresent(t -> builder.from(VelocityUtils.merge(t, context)));
      toTemplate.ifPresent(t -> builder.to(VelocityUtils.merge(t, context)));
      ccTemplate.ifPresent(t -> builder.cc(VelocityUtils.merge(t, context)));
      bccTemplate.ifPresent(t -> builder.bcc(VelocityUtils.merge(t, context)));
      subjectTemplate.ifPresent(t -> builder.subject(VelocityUtils.merge(t, context)));
      bodyTemplate.ifPresent(t -> builder.body(VelocityUtils.merge(t, context)));
      return Optional.of(new SendMailRuleToPluginActionMsg(toDeviceActorMsg.getTenantId(),
          toDeviceActorMsg.getCustomerId(), toDeviceActorMsg.getDeviceId(), builder.build()));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<ToDeviceMsg> convert(PluginToRuleMsg<?> response) {
    if (response instanceof ResponsePluginToRuleMsg) {
      return Optional.of(((ResponsePluginToRuleMsg) response).getPayload());
    }
    return Optional.empty();
  }

  @Override
  public boolean isOneWayAction() {
    return true;
  }

}

package org.iotp.analytics.ruleengine.core.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.tools.generic.DateTool;
import org.iotp.analytics.ruleengine.api.device.DeviceAttributes;
import org.iotp.analytics.ruleengine.api.device.DeviceMetaData;
import org.iotp.analytics.ruleengine.api.rules.RuleProcessingMetaData;
import org.iotp.analytics.ruleengine.common.msg.core.TelemetryUploadRequest;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.analytics.ruleengine.core.filter.NashornJsEvaluator;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.kv.BasicTsKvEntry;

/**
 */
public class VelocityUtils {

  public static Template create(String source, String templateName) throws ParseException {
    RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
    StringReader reader = new StringReader(source);
    SimpleNode node = runtimeServices.parse(reader, templateName);
    Template template = new Template();
    template.setRuntimeServices(runtimeServices);
    template.setData(node);
    template.initDocument();
    return template;
  }

  public static String merge(Template template, VelocityContext context) {
    StringWriter writer = new StringWriter();
    template.merge(context, writer);
    return writer.toString();
  }

  public static VelocityContext createContext(RuleProcessingMetaData metadata) {
    VelocityContext context = new VelocityContext();
    metadata.getValues().forEach((k, v) -> context.put(k, v));
    return context;
  }

  public static VelocityContext createContext(DeviceMetaData deviceMetaData, FromDeviceMsg payload) {
    VelocityContext context = new VelocityContext();
    context.put("date", new DateTool());
    context.put("math", new org.apache.velocity.tools.generic.MathTool());

    DeviceAttributes deviceAttributes = deviceMetaData.getDeviceAttributes();

    pushAttributes(context, deviceAttributes.getClientSideAttributes(), NashornJsEvaluator.CLIENT_SIDE);
    pushAttributes(context, deviceAttributes.getServerSideAttributes(), NashornJsEvaluator.SERVER_SIDE);
    pushAttributes(context, deviceAttributes.getServerSidePublicAttributes(), NashornJsEvaluator.SHARED);

    switch (payload.getMsgType()) {
    case POST_TELEMETRY_REQUEST:
      pushTsEntries(context, (TelemetryUploadRequest) payload);
      break;
    }

    context.put("deviceId", deviceMetaData.getDeviceId().getId().toString());
    context.put("deviceName", deviceMetaData.getDeviceName());
    context.put("deviceType", deviceMetaData.getDeviceType());

    return context;
  }

  private static void pushTsEntries(VelocityContext context, TelemetryUploadRequest payload) {
    payload.getData().forEach((k, vList) -> {
      vList.forEach(v -> {
        context.put(v.getKey(), new BasicTsKvEntry(k, v));
      });
    });
  }

  private static void pushAttributes(VelocityContext context, Collection<AttributeKvEntry> deviceAttributes,
      String prefix) {
    Map<String, String> values = new HashMap<>();
    deviceAttributes.forEach(v -> values.put(v.getKey(), v.getValueAsString()));
    context.put(prefix, values);
  }
}

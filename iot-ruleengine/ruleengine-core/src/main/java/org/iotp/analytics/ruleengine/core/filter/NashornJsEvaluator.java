package org.iotp.analytics.ruleengine.core.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.iotp.analytics.ruleengine.api.device.DeviceAttributes;
import org.iotp.analytics.ruleengine.common.msg.core.UpdateAttributesRequest;
import org.iotp.infomgt.data.kv.AttributeKvEntry;
import org.iotp.infomgt.data.kv.KvEntry;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class NashornJsEvaluator {

  public static final String CLIENT_SIDE = "cs";
  public static final String SERVER_SIDE = "ss";
  public static final String SHARED = "shared";
  private static NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

  private CompiledScript engine;

  public NashornJsEvaluator(String script) {
    engine = compileScript(script);
  }

  private static CompiledScript compileScript(String script) {
    ScriptEngine engine = factory.getScriptEngine(new String[] { "--no-java" });
    Compilable compEngine = (Compilable) engine;
    try {
      return compEngine.compile(script);
    } catch (ScriptException e) {
      log.warn("Failed to compile filter script: {}", e.getMessage(), e);
      throw new IllegalArgumentException("Can't compile script: " + e.getMessage());
    }
  }

  public static Bindings convertListEntries(Bindings bindings, String attributesVarName,
      Collection<AttributeKvEntry> attributes) {
    Map<String, Object> attrMap = new HashMap<>();
    for (AttributeKvEntry attr : attributes) {
      if (!CLIENT_SIDE.equalsIgnoreCase(attr.getKey()) && !SERVER_SIDE.equalsIgnoreCase(attr.getKey())
          && !SHARED.equalsIgnoreCase(attr.getKey())) {
        bindings.put(attr.getKey(), getValue(attr));
      }
      attrMap.put(attr.getKey(), getValue(attr));
    }
    bindings.put(attributesVarName, attrMap);
    return bindings;
  }

  public static Bindings updateBindings(Bindings bindings, UpdateAttributesRequest msg) {
    Map<String, Object> attrMap = (Map<String, Object>) bindings.get(CLIENT_SIDE);
    for (AttributeKvEntry attr : msg.getAttributes()) {
      if (!CLIENT_SIDE.equalsIgnoreCase(attr.getKey()) && !SERVER_SIDE.equalsIgnoreCase(attr.getKey())
          && !SHARED.equalsIgnoreCase(attr.getKey())) {
        bindings.put(attr.getKey(), getValue(attr));
      }
      attrMap.put(attr.getKey(), getValue(attr));
    }
    bindings.put(CLIENT_SIDE, attrMap);
    return bindings;
  }

  protected static Object getValue(KvEntry attr) {
    switch (attr.getDataType()) {
    case STRING:
      return attr.getStrValue().get();
    case LONG:
      return attr.getLongValue().get();
    case DOUBLE:
      return attr.getDoubleValue().get();
    case BOOLEAN:
      return attr.getBooleanValue().get();
    }
    return null;
  }

  public static Bindings toBindings(List<KvEntry> entries) {
    return toBindings(new SimpleBindings(), entries);
  }

  public static Bindings toBindings(Bindings bindings, List<KvEntry> entries) {
    for (KvEntry entry : entries) {
      bindings.put(entry.getKey(), getValue(entry));
    }
    return bindings;
  }

  public static Bindings getAttributeBindings(DeviceAttributes attributes) {
    Bindings bindings = new SimpleBindings();
    convertListEntries(bindings, CLIENT_SIDE, attributes.getClientSideAttributes());
    convertListEntries(bindings, SERVER_SIDE, attributes.getServerSideAttributes());
    convertListEntries(bindings, SHARED, attributes.getServerSidePublicAttributes());
    return bindings;
  }

  public Boolean execute(Bindings bindings) throws ScriptException {
    Object eval = engine.eval(bindings);
    if (eval instanceof Boolean) {
      return (Boolean) eval;
    } else {
      log.warn("Wrong result type: {}", eval);
      throw new ScriptException("Wrong result type: " + eval);
    }
  }

  public void destroy() {
    engine = null;
  }
}

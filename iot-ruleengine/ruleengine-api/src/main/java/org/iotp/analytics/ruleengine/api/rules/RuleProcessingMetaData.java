package org.iotp.analytics.ruleengine.api.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RuleProcessingMetaData {

  private final Map<String, Object> md;

  public RuleProcessingMetaData() {
    super();
    this.md = new HashMap<>();
  }

  public <T> void put(String key, T value) {
    md.put(key, value);
  }

  public <T> Optional<T> get(String key) {
    return Optional.ofNullable((T) md.get(key));
  }

  public Map<String, Object> getValues() {
    return Collections.unmodifiableMap(md);
  }
}

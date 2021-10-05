package io.resys.wrench.assets.script.api;

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.execution.Service;

public interface ScriptRepository {

  ScriptBuilder createBuilder();

  interface ScriptBuilder {
    ScriptBuilder src(InputStream src);
    ScriptBuilder src(JsonNode src);
    ScriptBuilder src(String src);
    ScriptBuilder rev(Integer rev);
    Service build();
  }

  interface ScriptContext {
    <T> T get(Class<T> type);
  }
}

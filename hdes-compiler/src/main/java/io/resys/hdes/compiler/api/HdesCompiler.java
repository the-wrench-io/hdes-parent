package io.resys.hdes.compiler.api;

import java.util.List;

import org.immutables.value.Value;

public interface HdesCompiler {
  
  Parser parser();
  
  interface Parser {
    Parser add(String fileName, String src);
    Code build();
  }

  @Value.Immutable
  interface Code {
    List<CodeValue> getValues();
  }
  
  @Value.Immutable
  interface CodeValue {
    String getType();
    String getSource();
    String getTarget();
    Class<?> getValue();
  }
}
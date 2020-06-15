package io.resys.hdes.runtime.api;

import java.util.List;

import org.immutables.value.Value;

import io.resys.hdes.compiler.api.HdesCompiler.HdesExecutable;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;

public interface HdesRuntime {
  
  interface Builder {
    Builder from(List<Resource> resources);
    RuntimeEnvir build();
  }

  interface RuntimeEnvir {
    RuntimeTask get(String name) throws ClassNotFoundException;
  }
  
  @Value.Immutable
  interface RuntimeTask {
    String getName();
    HdesExecutable getValue();
    Class<?> getInput();
    Class<?> getOutput();
  }
}

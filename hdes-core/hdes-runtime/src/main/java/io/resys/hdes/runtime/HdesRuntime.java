package io.resys.hdes.runtime;


public interface HdesRuntime {
  
  interface EnvirBuilder {
    ResourceBuilder add();
    HdesRuntimeEnvir build();
  }
  
  interface ResourceBuilder {
    ResourceBuilder name(String name);
    ResourceBuilder pkg(String pkg);
    ResourceBuilder src(String src);
    EnvirBuilder build();
  }
  
  interface HdesRuntimeEnvir {
    <T> T get(String name);
  }
}

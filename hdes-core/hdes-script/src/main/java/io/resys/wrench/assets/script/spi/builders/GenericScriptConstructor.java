package io.resys.wrench.assets.script.spi.builders;

import io.resys.hdes.client.api.programs.ServiceProgram.ServiceInit;

public class GenericScriptConstructor implements ServiceInit {

  @Override
  public <T> T get(Class<T> type) {
    try {
      return type.newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
}

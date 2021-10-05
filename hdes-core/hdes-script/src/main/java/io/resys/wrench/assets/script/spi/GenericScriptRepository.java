package io.resys.wrench.assets.script.spi;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.wrench.assets.script.api.ScriptRepository;
import io.resys.wrench.assets.script.spi.builders.GenericScriptBuilder;

public class GenericScriptRepository implements ScriptRepository {
  private final HdesAstTypes dataTypeRepository;

  public GenericScriptRepository( 
      HdesAstTypes dataTypeRepository) {
    super();
    this.dataTypeRepository = dataTypeRepository;
  }

  @Override
  public ScriptBuilder createBuilder() {
    return new GenericScriptBuilder(dataTypeRepository);
  }
}

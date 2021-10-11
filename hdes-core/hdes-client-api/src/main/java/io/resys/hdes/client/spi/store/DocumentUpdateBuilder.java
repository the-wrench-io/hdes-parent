package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.HdesStore.UpdateAstType;
import io.resys.hdes.client.api.HdesStore.UpdateBuilder;
import io.resys.hdes.client.api.ast.AstBody;
import io.smallrye.mutiny.Uni;

public class DocumentUpdateBuilder extends PersistenceCommands implements UpdateBuilder {

  public DocumentUpdateBuilder(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<Entity<AstBody>> build(UpdateAstType updateType) {
    // TODO Auto-generated method stub
    return null;
  }


}

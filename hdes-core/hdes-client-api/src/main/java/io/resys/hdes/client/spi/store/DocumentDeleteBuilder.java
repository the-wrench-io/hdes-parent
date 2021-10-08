package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore.DeleteAstType;
import io.resys.hdes.client.api.HdesStore.DeleteBuilder;
import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.ast.AstType;
import io.resys.hdes.client.spi.store.PersistenceConfig.EntityState;
import io.smallrye.mutiny.Uni;

public class DocumentDeleteBuilder extends PersistenceCommands implements DeleteBuilder {

  public DocumentDeleteBuilder(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<Entity<AstType>> build(DeleteAstType deleteType) {
    final Uni<EntityState<AstType>> query = get(deleteType.getId());
    
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

}

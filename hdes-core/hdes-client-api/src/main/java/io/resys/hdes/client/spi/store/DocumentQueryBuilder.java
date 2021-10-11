package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.HdesStore.QueryBuilder;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ast.AstBody;
import io.smallrye.mutiny.Uni;

public class DocumentQueryBuilder extends PersistenceCommands implements QueryBuilder {

  public DocumentQueryBuilder(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<StoreState> get() {
    return super.get();
  }

  @Override
  public Uni<Entity<AstBody>> get(String id) {
    var result = super.get(id);
    return result.onItem().transform(entityState->entityState.getEntity());
  }
}

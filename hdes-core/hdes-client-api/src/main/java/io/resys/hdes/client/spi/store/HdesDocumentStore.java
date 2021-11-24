package io.resys.hdes.client.spi.store;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.config.ThenaConfig;
import io.resys.hdes.client.api.config.ThenaConfig.EntityState;
import io.resys.hdes.client.spi.store.thena.DocumentQueryBuilder;
import io.resys.hdes.client.spi.store.thena.PersistenceCommands;
import io.smallrye.mutiny.Uni;

public class HdesDocumentStore extends PersistenceCommands implements HdesStore {

  
  public HdesDocumentStore(ThenaConfig config) {
    super(config);
  }

  @Override
  public QueryBuilder query() {
    return new DocumentQueryBuilder(config);
  }

  @Override
  public Uni<StoreEntity> create(CreateStoreEntity newType) {
    final var gid = gid(newType.getBodyType());
    final StoreEntity entity = ImmutableStoreEntity.builder()
      .id(gid)
      .bodyType(newType.getBodyType())
      .body(newType.getBody())
      .build();
    return super.save(entity);
  }

  @Override
  public Uni<StoreEntity> update(UpdateStoreEntity updateType) {
   
    
    final Uni<EntityState> query = getEntityState(updateType.getId());
    return query.onItem().transformToUni(state -> {
      final StoreEntity entity = ImmutableStoreEntity.builder()
          .id(updateType.getId())
          .bodyType(state.getEntity().getBodyType())
          .body(updateType.getBody())
          .build();
      return super.save(entity);
    });
  }

  @Override
  public Uni<StoreEntity> delete(DeleteAstType deleteType) {
    final Uni<EntityState> query = getEntityState(deleteType.getId());
    
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  private String gid(AstBodyType type) {
    return config.getGidProvider().getNextId(type);
  }

}

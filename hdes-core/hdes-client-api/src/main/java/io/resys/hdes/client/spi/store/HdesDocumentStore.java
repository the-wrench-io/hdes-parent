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
import io.resys.hdes.client.api.HdesStore.CreateAstType;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.ast.ImmutableAstDecision;
import io.resys.hdes.client.api.ast.ImmutableAstService;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.spi.store.PersistenceConfig.EntityState;
import io.smallrye.mutiny.Uni;

public class HdesDocumentStore extends PersistenceCommands implements HdesStore {

  
  public HdesDocumentStore(PersistenceConfig config) {
    super(config);
  }



  @Override
  public QueryBuilder query() {
    return new DocumentQueryBuilder(config);
  }



  @Override
  public Uni<StoreEntity> create(CreateAstType newType) {
    Uni<StoreEntity> result;
    switch (newType.getType()) {
    case FLOW: result = flow(newType.getId()); break;
    case DT: result = decision(newType.getId()); break;
    case FLOW_TASK: result = service(newType.getId()); break;
    default: throw new RuntimeException("Unrecognized type:" + newType.getType());
    }
    return result;
  }



  @Override
  public Uni<StoreEntity> update(UpdateAstType updateType) {
    final StoreEntity entity = ImmutableStoreEntity.builder()
        .id(updateType.getId())
        .type(updateType.getType())
        .body(updateType.getBody())
        .build();
    
    return super.save(entity); 
  }



  @Override
  public Uni<StoreEntity> delete(DeleteAstType deleteType) {
    final Uni<EntityState> query = getEntityState(deleteType.getId());
    
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }


  protected Uni<StoreEntity> flow(String name) {
    final var gid = gid(AstBodyType.FLOW);
    final StoreEntity entity = initializeFlow(name, gid);
    return super.save(entity);
  }

  protected Uni<StoreEntity> decision(String name) {
    final var gid = gid(AstBodyType.DT);
    final var decision = ImmutableAstDecision.builder()
        .name(name)
        .build();
    final StoreEntity entity = ImmutableStoreEntity.builder()
        .id(gid)
        .type(AstBodyType.DT)
        .body(decision.getCommands())
        .build();
    
    return super.save(entity);
  }

  protected Uni<StoreEntity> service(String name) {
    final var gid = gid(AstBodyType.FLOW_TASK);
    final var decision = ImmutableAstService.builder()
        .name(name)
        .build();
    final StoreEntity entity = ImmutableStoreEntity.builder()
        .id(gid)
        .type(AstBodyType.FLOW_TASK)
        .body(decision.getCommands())
        .build();
    
    return super.save(entity);
  }

  private StoreEntity initializeFlow(String name, final String gid) {
    final StoreEntity entity = ImmutableStoreEntity.builder()
        .id(gid)
        .type(AstBodyType.FLOW)
        .addBody(ImmutableAstCommand.builder().type(AstCommandValue.ADD).id("0").value("id: " + name).build())
        .addBody(ImmutableAstCommand.builder().type(AstCommandValue.ADD).id("1").value("description: ").build())
        .build();
    return entity;
  }

  
  private String gid(AstBodyType type) {
    return config.getGidProvider().getNextId(type);
  }

}

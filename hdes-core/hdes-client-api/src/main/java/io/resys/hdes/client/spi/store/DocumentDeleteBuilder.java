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

import io.resys.hdes.client.api.HdesStore.DeleteAstType;
import io.resys.hdes.client.api.HdesStore.DeleteBuilder;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.spi.store.PersistenceConfig.EntityState;
import io.smallrye.mutiny.Uni;

public class DocumentDeleteBuilder extends PersistenceCommands implements DeleteBuilder {

  public DocumentDeleteBuilder(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<StoreEntity> build(DeleteAstType deleteType) {
    final Uni<EntityState<AstBody>> query = get(deleteType.getId());
    
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

}

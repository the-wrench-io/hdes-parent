package io.resys.hdes.client.spi.store;

import java.util.Collection;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.HdesStore.StoreExceptionMsg;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ImmutableStoreExceptionMsg;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.exceptions.StoreException;
import io.resys.hdes.client.spi.store.PersistenceConfig.EntityState;
import io.resys.thena.docdb.api.actions.CommitActions.CommitResult;

/*-
 * #%L
 * stencil-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
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

import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.resys.thena.docdb.api.actions.ObjectsActions.BlobObject;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.smallrye.mutiny.Uni;


public class PersistenceCommands implements PersistenceConfig.Commands {
  protected final PersistenceConfig config;

  public PersistenceCommands(PersistenceConfig config) {
    super();
    this.config = config;
  }
  
  @Override
  public <T extends AstBody> Uni<StoreEntity> delete(StoreEntity toBeDeleted) {
    return config.getClient().commit().head()
        .head(config.getRepoName(), config.getHeadName())
        .message("delete type: '" + toBeDeleted.getType() + "', with id: '" + toBeDeleted.getId() + "'")
        .parentIsLatest()
        .author(config.getAuthorProvider().getAuthor())
        .remove(toBeDeleted.getId())
        .build().onItem().transform(commit -> {
          if(commit.getStatus() == CommitStatus.OK) {
            return toBeDeleted;
          }
          // TODO
          throw new StoreException("DELETE_FAIL", toBeDeleted, convertMessages(commit));
        });
  }
  
  private StoreExceptionMsg convertMessages(CommitResult commit) {
    return ImmutableStoreExceptionMsg.builder()
        .addAllArgs(commit.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
        .build();
  }


  
  @Override
  public <T extends AstBody> Uni<StoreEntity> save(StoreEntity toBeSaved) {
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("update type: '" + toBeSaved.getType() + "', with id: '" + toBeSaved.getId() + "'")
      .parentIsLatest()
      .author(config.getAuthorProvider().getAuthor())
      .append(toBeSaved.getId(), config.getSerializer().toString(toBeSaved))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return toBeSaved;
        }
        // TODO
        throw new StoreException("DELETE_FAIL", toBeSaved, convertMessages(commit));
      });
  }

  @Override
  public Uni<Collection<StoreEntity>> save(Collection<StoreEntity> entities) {
    final var commitBuilder = config.getClient().commit().head().head(config.getRepoName(), config.getHeadName());
    final StoreEntity first = entities.iterator().next();
    
    for(final var target : entities) {
      commitBuilder.append(target.getId(), config.getSerializer().toString(target));
    }
    
    return commitBuilder
        .message("update type: '" + first.getType() + "', with id: '" + first.getId() + "'")
        .parentIsLatest()
        .author(config.getAuthorProvider().getAuthor())
        .build().onItem().transform(commit -> {
          if(commit.getStatus() == CommitStatus.OK) {
            return entities;
          }
          // TODO
          throw new StoreException("SAVE_FAIL", null, convertMessages(commit));
        });
  }

  @Override
  public Uni<StoreState> get() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends AstBody> Uni<EntityState<T>> get(String id) {
    return config.getClient()
        .objects().blobState()
        .repo(config.getRepoName())
        .anyId(config.getHeadName())
        .blobName(id)
        .get().onItem()
        .transform(state -> {
          if(state.getStatus() != ObjectsStatus.OK) {
            // TODO
            throw new StoreException("GET_FAIL", null, convertMessages(state));
          }
          StoreEntity start = (StoreEntity) config.getDeserializer()
              .fromString(state.getObjects().getBlob().getValue());
          
          var result = ImmutableEntityState.<T>builder()
              .src(state)
              .entity(start)
              .build();
          return result;
        });
  }

  private StoreExceptionMsg convertMessages(ObjectsResult<BlobObject> state) {
    return ImmutableStoreExceptionMsg.builder()
        .addAllArgs(state.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
        .build();
  }
}

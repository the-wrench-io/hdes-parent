package io.resys.hdes.client.spi.store;

import java.util.List;
import java.util.stream.Collectors;

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

import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.HdesStore.UpdateAstType;
import io.resys.hdes.client.api.HdesStore.UpdateBuilder;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ImmutableStoreExceptionMsg;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.exceptions.StoreException;
import io.resys.thena.docdb.api.actions.CommitActions.CommitResult;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.smallrye.mutiny.Uni;

public class DocumentUpdateBuilder extends PersistenceCommands implements UpdateBuilder {

  public DocumentUpdateBuilder(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<StoreEntity> build(UpdateAstType updateType) {
    final var gid = gid(updateType.getType());

    final StoreEntity entity = ImmutableStoreEntity.builder()
        .id(gid)
        .type(updateType.getType())
        .value("")
        .body(updateType.getBody())
        .build();
    
    String message = "Update type";
    String code = "UPDATE"; //TODO
    return saveCommit(gid, entity, message, code);
  }

  private Uni<StoreEntity> saveCommit(
      final String gid, final StoreEntity entity, String message,
      String code) {
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message(message)
      .parentIsLatest()
      .author(getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return entity;
        }
        throw new StoreException(code, (StoreEntity)entity,
            ImmutableStoreExceptionMsg.builder()
            .addAllArgs(getCommitMessages(commit))
            .build());
      });
  }
  
  private String gid(AstBodyType type) {
    return config.getGidProvider().getNextId(type);
  }
  
  private List<String> getCommitMessages(CommitResult commit) {
    return commit.getMessages().stream().map(commitMessage->commitMessage.getText()).collect(Collectors.toList());
  }
  
  private String getAuthor() {
    return config.getAuthorProvider().getAuthor();
  }
}

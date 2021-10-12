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

import java.util.List;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.HdesStore.CreateAstType;
import io.resys.hdes.client.api.HdesStore.CreateBuilder;
import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.ImmutableEntity;
import io.resys.hdes.client.api.ImmutableStoreExceptionMsg;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstBody.EntityType;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.ImmutableAstDecision;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.api.ast.ImmutableAstService;
import io.resys.hdes.client.api.ast.ImmutableHeaders;
import io.resys.hdes.client.api.exceptions.StoreException;
import io.resys.hdes.client.spi.flow.FlowAstBuilderImpl;
import io.resys.thena.docdb.api.actions.CommitActions.CommitResult;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.smallrye.mutiny.Uni;


public class DocumentCreateBuilder implements CreateBuilder {

  private final PersistenceConfig config;
  
  public DocumentCreateBuilder(PersistenceConfig config) {
    super();
    this.config = config;
  }
  
  @Override
  public Uni<Entity<AstFlow>> flow(String name) {
    final var gid = gid(EntityType.FLOW);
    final var flow = ImmutableAstFlow.builder()
        .name(name)
//        .rev(1)
//        .headers(ImmutableHeaders.builder().build())
//        .bodyType(EntityType.FLOW)
//        .src(null)
//        .source("")
        .build();
    //TODO: initialize flow from template
    final Entity<AstFlow> entity = ImmutableEntity.<AstFlow>builder()
        .id(gid)
        .type(EntityType.FLOW)
        .body(flow)
        .build();
    
    String message = "creating-flow";
    String code = "CREATE_FLOW"; //TODO
    return saveCommit(gid, entity, message, code);

  }



  @Override
  public Uni<Entity<AstDecision>> decision(String name) {
    final var gid = gid(EntityType.DT);
    final var decision = ImmutableAstDecision.builder()
        .name(name)
        .build();
    final Entity<AstDecision> entity = ImmutableEntity.<AstDecision>builder()
        .id(gid)
        .type(EntityType.DT)
        .body(decision)
        .build();
    
    String message = "creating-decision";
    String code = "CREATE_DECISION"; //TODO
    return saveCommit(gid, entity, message, code);
  }

  @Override
  public Uni<Entity<AstService>> service(String name) {
    final var gid = gid(EntityType.FLOW_TASK);
    final var decision = ImmutableAstService.builder()
        .name(name)
        .build();
    final Entity<AstService> entity = ImmutableEntity.<AstService>builder()
        .id(gid)
        .type(EntityType.FLOW_TASK)
        .body(decision)
        .build();
    
    String message = "creating-service";
    String code = "CREATE_SERVICE"; //TODO
    return saveCommit(gid, entity, message, code);
  }



  @Override
  public Uni<Entity<AstBody>> build(CreateAstType newType) {
    Uni<?> result;
    switch (newType.getType()) {
    case FLOW: result = flow(newType.getName()); break;
    case DT: result = decision(newType.getName()); break;
    case FLOW_TASK: result = service(newType.getName()); break;
    default: throw new RuntimeException("Unrecognized type:" + newType.getType());
    }
    return (Uni<Entity<AstBody>>) result;
  }

  private <T extends AstBody> Uni<Entity<T>> saveCommit(
      final String gid, final Entity<T> entity, String message,
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
        throw new StoreException(code, (Entity<AstBody>)entity,
            ImmutableStoreExceptionMsg.builder()
            .addAllArgs(getCommitMessages(commit))
            .build());
      });
  }
  
  private String gid(EntityType type) {
    return config.getGidProvider().getNextId(type);
  }
  
  private List<String> getCommitMessages(CommitResult commit) {
    return commit.getMessages().stream().map(commitMessage->commitMessage.getText()).collect(Collectors.toList());
  }

  private String getAuthor() {
    return config.getAuthorProvider().getAuthor();
  }
}

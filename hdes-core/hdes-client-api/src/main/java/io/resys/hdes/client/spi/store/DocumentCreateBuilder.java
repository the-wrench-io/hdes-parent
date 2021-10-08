package io.resys.hdes.client.spi.store;

import java.util.Optional;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.HdesStore.CreateAstType;
import io.resys.hdes.client.api.HdesStore.CreateBuilder;
import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.HdesStore.EntityType;
import io.resys.hdes.client.api.ImmutableEntity;
import io.resys.hdes.client.api.ImmutableStoreExceptionMsg;
import io.resys.hdes.client.api.ast.AstType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.FlowAstType;
import io.resys.hdes.client.api.ast.ImmutableAstDataType;
import io.resys.hdes.client.api.ast.ImmutableAstHeaders;
import io.resys.hdes.client.api.ast.ImmutableDecisionAstType;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.hdes.client.api.ast.ImmutableServiceAstType;
import io.resys.hdes.client.api.ast.ServiceAstType;
import io.resys.hdes.client.api.exceptions.DataTypeException;
import io.resys.hdes.client.api.exceptions.StoreException;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.smallrye.mutiny.Uni;


public class DocumentCreateBuilder implements CreateBuilder {

  private final PersistenceConfig config;
  
  public DocumentCreateBuilder(PersistenceConfig config) {
    super();
    this.config = config;
  }
  
  @Override
  public Uni<Entity<FlowAstType>> flow(String name) {
    final var gid = gid(EntityType.FLOW);
    final var flow = ImmutableFlowAstType.builder()
        .name(name)
        .rev(1)
        //.headers(ImmutableAstHeaders.builder()
        //    .addInputs(ImmutableAstDataType.builder().))
        //.src("")
        .build();
    //TODO: initialize flow from template
    final Entity<FlowAstType> entity = ImmutableEntity.<FlowAstType>builder()
        .id(gid)
        .type(EntityType.FLOW)
        .body(flow)
        .build();
    
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("creating-flow")
      .parentIsLatest()
      .author(getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return entity;
        }
        String code = "CREATE_FLOW"; //TODO
        throw new StoreException(code, null,
            ImmutableStoreExceptionMsg.builder()
            .addAllArgs(commit.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
            .build());
      });

  }

  @Override
  public Uni<Entity<DecisionAstType>> decision(String name) {
    final var gid = gid(EntityType.DT);
    final var decision = ImmutableDecisionAstType.builder()
        .name(name)
        .build();
    final Entity<DecisionAstType> entity = ImmutableEntity.<DecisionAstType>builder()
        .id(gid)
        .type(EntityType.DT)
        .body(decision)
        .build();
    
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("creating-decision")
      .parentIsLatest()
      .author(getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return entity;
        }
        String code = "CREATE_DECISION"; //TODO
        throw new StoreException(code, null,
            ImmutableStoreExceptionMsg.builder()
            .addAllArgs(commit.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
            .build());
      });
  }

  @Override
  public Uni<Entity<ServiceAstType>> service(String name) {
    final var gid = gid(EntityType.FLOW_TASK);
    final var decision = ImmutableServiceAstType.builder()
        .name(name)
        .build();
    final Entity<ServiceAstType> entity = ImmutableEntity.<ServiceAstType>builder()
        .id(gid)
        .type(EntityType.FLOW_TASK)
        .body(decision)
        .build();
    
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("creating-service")
      .parentIsLatest()
      .author(getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return entity;
        }
        String code = "CREATE_SERVICE"; //TODO
        throw new StoreException(code, null,
            ImmutableStoreExceptionMsg.builder()
            .addAllArgs(commit.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
            .build());
      });
  }

  private String getAuthor() {
    return config.getAuthorProvider().getAuthor();
  }

  @Override
  public Uni<Entity<AstType>> build(CreateAstType newType) {
    /* TODO
    switch (newType.getType()) {
    case FLOW: return flow(newType.getName());
    case DT: return decision(newType.getName());
    case FLOW_TASK: return service(newType.getName());
    default: throw new RuntimeException("Unrecognized type:" + newType.getType());
    }
    */
    return null;
  }

  private String gid(EntityType type) {
    return config.getGidProvider().getNextId(type);
  }
}

package io.resys.hdes.client.spi.store;

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
import io.resys.hdes.client.api.exceptions.StoreException;
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
        .rev(1)
        //.headers(ImmutableAstHeaders.builder().build()
        //    .withInputs(ImmutableAstDataType.builder().name("test").valueType(ValueType.STRING).build()))
        //.src("")
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
        throw new StoreException(code, null,
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

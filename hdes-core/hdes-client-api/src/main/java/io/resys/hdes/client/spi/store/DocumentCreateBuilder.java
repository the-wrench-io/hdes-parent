package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore.CreateAstType;
import io.resys.hdes.client.api.HdesStore.CreateBuilder;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.ast.ImmutableAstDecision;
import io.resys.hdes.client.api.ast.ImmutableAstService;
import io.smallrye.mutiny.Uni;

public class DocumentCreateBuilder extends PersistenceCommands implements CreateBuilder {

  public DocumentCreateBuilder(PersistenceConfig config) {
    super(config);
  }
  
  @Override
  public Uni<StoreEntity> flow(String name) {
    final var gid = gid(AstBodyType.FLOW);
    final StoreEntity entity = initializeFlow(name, gid);
    return super.save(entity);
  }

  @Override
  public Uni<StoreEntity> decision(String name) {
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

  @Override
  public Uni<StoreEntity> service(String name) {
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



  @Override
  public Uni<StoreEntity> build(CreateAstType newType) {
    Uni<StoreEntity> result;
    switch (newType.getType()) {
    case FLOW: result = flow(newType.getName()); break;
    case DT: result = decision(newType.getName()); break;
    case FLOW_TASK: result = service(newType.getName()); break;
    default: throw new RuntimeException("Unrecognized type:" + newType.getType());
    }
    return result;
  }


  private StoreEntity initializeFlow(String name, final String gid) {
    final StoreEntity entity = ImmutableStoreEntity.builder()
        .id(gid)
        .type(AstBodyType.FLOW)
        .value("")
        .addBody(ImmutableAstCommand.builder().type(AstCommandValue.ADD).id("0").value("id: " + name).build())
        .addBody(ImmutableAstCommand.builder().type(AstCommandValue.ADD).id("1").value("description: ").build())
        .build();
    return entity;
  }

  
  private String gid(AstBodyType type) {
    return config.getGidProvider().getNextId(type);
  }

}

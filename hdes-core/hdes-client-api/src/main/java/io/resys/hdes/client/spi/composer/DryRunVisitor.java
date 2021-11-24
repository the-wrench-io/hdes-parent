package io.resys.hdes.client.spi.composer;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer.ComposerEntity;
import io.resys.hdes.client.api.HdesComposer.ComposerState;
import io.resys.hdes.client.api.HdesComposer.UpdateEntity;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ImmutableComposerState;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.exceptions.ComposerException;

public class DryRunVisitor {
  private final HdesClient client;
  
  public DryRunVisitor(HdesClient client) {
    super();
    this.client = client;
  }

  public ComposerEntity<?> visit(StoreState source, UpdateEntity entity) {
    // create envir
    final var envirBuilder = client.envir();
    source.getDecisions().values().stream()
      .filter(e -> !e.getId().equals(entity.getId()))
      .forEach(v -> envirBuilder.addCommand().id(v.getId()).decision(v).build());
    
    source.getServices().values().stream()
      .filter(e -> !e.getId().equals(entity.getId()))
      .forEach(v -> envirBuilder.addCommand().id(v.getId()).service(v).build());
    
    source.getFlows().values().stream()
      .filter(e -> !e.getId().equals(entity.getId()))
      .forEach(v -> envirBuilder.addCommand().id(v.getId()).flow(v).build());
    
    // get the old entity
    final StoreEntity oldEntity;
    if(source.getDecisions().containsKey(entity.getId())) {
      oldEntity = source.getDecisions().get(entity.getId());
    } else if(source.getServices().containsKey(entity.getId())) {
      oldEntity = source.getServices().get(entity.getId());;
    } else if(source.getFlows().containsKey(entity.getId())) {
      oldEntity = source.getFlows().get(entity.getId());;
    } else {
      throw new ComposerException("Unknown entity: '" + entity.getId() + "'!");
    }
    
    // Merge new data to old
    final StoreEntity newEntity = ImmutableStoreEntity.builder().from(oldEntity).body(entity.getBody()).build();
    switch (oldEntity.getBodyType()) {
    case DT:
      envirBuilder.addCommand().decision(newEntity).build();
      break;
    case FLOW:
      envirBuilder.addCommand().flow(newEntity).build();
      break;      
    case FLOW_TASK: 
      envirBuilder.addCommand().service(newEntity).build();
    default: throw new ComposerException("Unknown entity: '" + entity.getId() + "'!");
    }
    
    // map envir
    final var envir = envirBuilder.build();
    final var builder = ImmutableComposerState.builder();
    envir.getValues().values().forEach(v -> ComposerEntityMapper.toComposer(builder, v));
    final ComposerState state = builder.build();
    
    // return dry run
    switch (oldEntity.getBodyType()) {
    case DT: return state.getDecisions().get(entity.getId());
    case FLOW: return state.getFlows().get(entity.getId());     
    case FLOW_TASK: return state.getServices().get(entity.getId());
    default: throw new ComposerException("Failed to dry run entity: '" + entity.getId() + "'!");
    }
  }

}

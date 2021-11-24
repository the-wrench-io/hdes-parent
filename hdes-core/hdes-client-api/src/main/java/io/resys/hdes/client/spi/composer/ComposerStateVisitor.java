package io.resys.hdes.client.spi.composer;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer.ComposerState;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ImmutableComposerState;

public class ComposerStateVisitor {
  private final HdesClient client;
  
  public ComposerStateVisitor(HdesClient client) {
    super();
    this.client = client;
  }

  public ComposerState visit(StoreState source) {
    // create envir
    final var envirBuilder = client.envir();
    source.getDecisions().values().forEach(v -> envirBuilder.addCommand().id(v.getId()).decision(v).build());
    source.getServices().values().forEach(v -> envirBuilder.addCommand().id(v.getId()).service(v).build());
    source.getFlows().values().forEach(v -> envirBuilder.addCommand().id(v.getId()).flow(v).build());
    final var envir = envirBuilder.build();
    
    // map envir
    final var builder = ImmutableComposerState.builder();
    envir.getValues().values().forEach(v -> ComposerEntityMapper.toComposer(builder, v));
    return (ComposerState) builder.build(); 
  }
}

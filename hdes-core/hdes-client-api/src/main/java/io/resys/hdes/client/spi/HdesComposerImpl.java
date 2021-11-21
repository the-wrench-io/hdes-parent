package io.resys.hdes.client.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ImmutableComposerEntity;
import io.resys.hdes.client.api.ImmutableComposerState;
import io.resys.hdes.client.api.ImmutableUpdateStoreEntity;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.programs.ServiceData;
import io.resys.hdes.client.spi.datadump.DataDumpBuilder;
import io.smallrye.mutiny.Uni;

public class HdesComposerImpl implements HdesComposer {

  private final HdesClient client;

  public HdesComposerImpl(HdesClient client) {
    super();
    this.client = client;
  }

  @Override
  public Uni<ComposerState> get() {
    return client.store().query().get().onItem().transform(this::buildComposerState);
  }
  
  @Override
  public Uni<ComposerState> update(UpdateEntity asset) {
    return client.store().update(ImmutableUpdateStoreEntity.builder().id(asset.getId()).body(asset.getBody()).build())
        .onItem().transformToUni((updated) ->  
          client.store().query().get().onItem().transform(this::buildComposerState)
        );
  }

  @Override
  public Uni<ComposerState> create(CreateEntity asset) {
    final List<AstCommand> commands = new ArrayList<>();
    switch (asset.getType()) {
    case DT: commands.addAll(createDecision(asset)); break;
    case FLOW: commands.addAll(createFlow(asset)); break;
    case FLOW_TASK: commands.addAll(createFlowTask(asset)); break;
    case TAG: break;
    }
    
    return null;
  }

  @Override
  public Uni<ComposerState> delete(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<ComposerEntity<?>> get(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<List<ComposerEntity<?>>> getHistory(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<ComposerState> copyAs(CopyAs copyAs) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<DebugResponse> debug(DebugRequest entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<ComposerEntity<?>> dryRun(UpdateEntity entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<StoreDump> getStoreDump() {
    return client.store().query().get().onItem().transform(state -> new DataDumpBuilder(client.mapper()).build(state));
  }

  
  private ComposerState buildComposerState(StoreState source) {
    // create envir
    final var envirBuilder = client.envir();
    source.getDecisions().values().forEach(v -> envirBuilder.addCommand().id(v.getId()).decision(v).build());
    source.getServices().values().forEach(v -> envirBuilder.addCommand().id(v.getId()).service(v).build());
    source.getFlows().values().forEach(v -> envirBuilder.addCommand().id(v.getId()).flow(v).build());
    final var envir = envirBuilder.build();
    
    // map envir
    final var builder = ImmutableComposerState.builder();
    for(final var wrapper : envir.getValues().values()) {
      switch (wrapper.getSource().getBodyType()) {
      case DT:
        final var dt = ImmutableComposerEntity.<AstDecision>builder()
          .id(wrapper.getId())
          .ast((AstDecision) wrapper.getAst().orElse(null))
          .status(wrapper.getStatus())
          .errors(wrapper.getErrors())
          .warnings(wrapper.getWarnings())
          .associations(wrapper.getAssociations())
          .source(wrapper.getSource())
          .build();
        builder.putDecisions(dt.getId(), dt);
        break;
      case FLOW:
        final var flow = ImmutableComposerEntity.<AstFlow>builder()
          .id(wrapper.getId())
          .ast((AstFlow) wrapper.getAst().orElse(null))
          .status(wrapper.getStatus())
          .errors(wrapper.getErrors())
          .warnings(wrapper.getWarnings())
          .associations(wrapper.getAssociations())
          .source(wrapper.getSource())
          .build();
        builder.putFlows(flow.getId(), flow);
        break;
      case FLOW_TASK:
        final var service = ImmutableComposerEntity.<AstService>builder()
          .id(wrapper.getId())
          .ast((AstService) wrapper.getAst().orElse(null))
          .status(wrapper.getStatus())
          .errors(wrapper.getErrors())
          .warnings(wrapper.getWarnings())
          .associations(wrapper.getAssociations())
          .source(wrapper.getSource())
          .build();
        builder.putServices(service.getId(), service);
        break;
      default:
        break;
      }
    }
    return (ComposerState) builder.build(); 
  }

  public List<AstCommand> createFlow(CreateEntity entity) {
    final var lnr = System.lineSeparator();
    final var body = new StringBuilder()
        .append("id: ").append(entity.getName()).append(lnr)
        .toString();
    
    return Arrays.asList(ImmutableAstCommand.builder().type(AstCommandValue.SET_BODY).value(body).build());
  }

  public List<AstCommand> createFlowTask(CreateEntity entity) {
    final var lnr = System.lineSeparator();
    final var body = new StringBuilder()
        .append("package io.resys.wrench.assets.bundle.groovy;").append(lnr)
        .append("import java.io.Serializable;").append(lnr)
        .append("public class ").append(entity.getName()).append("{").append(lnr)
        .append("  public Output execute(Input input) {").append(lnr)
        .append("    return new Output();").append(lnr)
        .append("  }").append(lnr)

        .append("  @").append(ServiceData.class.getSimpleName()).append(lnr)
        .append("  public static class Input implements Serializable {").append(lnr)
        .append("  }").append(lnr)
        .append("  @").append(ServiceData.class.getSimpleName()).append(lnr)
        .append("  public static class Output implements Serializable {").append(lnr)
        .append("  }").append(lnr)
        .append("}").append(lnr)
        .toString();
    
    return Arrays.asList(ImmutableAstCommand.builder().type(AstCommandValue.SET_BODY).value(body).build());
  }

  public List<AstCommand> createDecision(CreateEntity entity) {
    return Arrays.asList(
      ImmutableAstCommand.builder().type(AstCommandValue.ADD_HEADER_IN).build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_REF).id("0").value("inputColumn").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_TYPE).id("0").value("STRING").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.ADD_HEADER_OUT).build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_REF).id("1").value("outputColumn").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_TYPE).id("1").value("STRING").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_NAME).value(entity.getName()).build()
    );
  }
}

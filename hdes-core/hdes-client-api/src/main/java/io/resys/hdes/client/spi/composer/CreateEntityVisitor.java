package io.resys.hdes.client.spi.composer;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer.ComposerState;
import io.resys.hdes.client.api.HdesComposer.CreateEntity;
import io.resys.hdes.client.api.HdesStore.CreateStoreEntity;
import io.resys.hdes.client.api.HdesStore.ImportStoreEntity;
import io.resys.hdes.client.api.ImmutableCreateStoreEntity;
import io.resys.hdes.client.api.ImmutableImportStoreEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.exceptions.ComposerException;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionLog;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionResult;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResultLog;
import io.resys.hdes.client.api.programs.Program.ProgramContext;
import io.resys.hdes.client.api.programs.ServiceData;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.api.programs.ServiceProgram.ServiceResult;
import io.resys.hdes.client.spi.changeset.AstCommandOptimiser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateEntityVisitor {

  private final HdesClient client;
  private final CreateEntity asset;
  private final ComposerState state;
  private final AstCommandOptimiser optimise;
  private final List<CreateStoreEntity> result = new ArrayList<>();
  
  public CreateEntityVisitor(ComposerState state, CreateEntity asset, HdesClient client) {
    super();
    this.asset = asset;
    this.state = state;
    this.client = client;
    this.optimise = new AstCommandOptimiser(client);
  }
  public ImportStoreEntity visit() {
    if(asset.getName() == null && asset.getBody().isEmpty()) {
      throw new ComposerException("Name can't be null if body is empty!");
    }
    visitValidations();
    visitBody();
    return ImmutableImportStoreEntity.builder()
        .addAllCreate(result)
        .build();
  }
  
  private void visitBody() {
    switch (asset.getType()) {
    case DT: initDecision(asset); break;
    case FLOW: initFlow(asset); break;
    case FLOW_TASK: initFlowTask(asset); break;
    case TAG: initTag(asset); break;
    case BRANCH: initBranch(asset); break;
    default: throw new ComposerException("Unknown asset: '" + asset.getType() + "'!"); 
    }
  }

  private void visitValidations() {
    
    // DT validations
    final var decision = state.getDecisions().values().stream()
      .filter(e -> e.getAst() != null)
      .filter(e -> e.getAst().getName().equals(asset.getName()))
      .findFirst();
    if(decision.isPresent()) {
      throw new ComposerException(decision.get().getSource().getBodyType() + " asset with name: '" + asset.getName() + "' exists already!");
    }
    
    // Flow validations
    final var flow = state.getFlows().values().stream()
        .filter(e -> e.getAst() != null)
        .filter(e -> e.getAst().getName().equals(asset.getName()))
        .findFirst();
    if(flow.isPresent()) {
      throw new ComposerException(flow.get().getSource().getBodyType() + " asset with name: '" + asset.getName() + "' exists already!");
    }
    
    // Flow task validations
    final var flowtask = state.getServices().values().stream()
        .filter(e -> e.getAst() != null)
        .filter(e -> e.getAst().getName().equals(asset.getName()))
        .findFirst();
    if(flowtask.isPresent()) {
      throw new ComposerException(flow.get().getSource().getBodyType() + " asset with name: '" + asset.getName() + "' exists already!");
    }
    
    // Tag validations
    final var tag = state.getTags().values().stream()
        .filter(e -> e.getAst() != null)
        .filter(e -> e.getAst().getName().equals(asset.getName()))
        .findFirst();
    if(tag.isPresent()) {
      throw new ComposerException(flow.get().getSource().getBodyType() + " asset with name: '" + asset.getName() + "' exists already!");
    }

    // Branch validations
    final var branch = state.getBranches().values().stream()
        .filter(e -> e.getAst() != null)
        .filter(e -> e.getAst().getName().equals(asset.getName()))
        .findFirst();
    if(branch.isPresent()) {
      throw new ComposerException(branch.get().getSource().getBodyType() + " asset with name: '" + asset.getName() + "' exists already!");
    }
  }

  public void initBranch(CreateEntity entity) {
    final List<CreateStoreEntity> createEntities = new CreateBranchVisitor(state).visitCommands(entity.getBody());
    result.addAll(createEntities);
  }

  public void initFlow(CreateEntity entity) {
    if(!entity.getBody().isEmpty()) {
      result.add(ImmutableCreateStoreEntity.builder()
          .bodyType(asset.getType())
          .body(entity.getBody())
          .build());
    }
    
    final var lnr = System.lineSeparator();
    final var body = new StringBuilder()
        .append("id: ").append(entity.getName()).append(lnr)
        .toString();
    
    final var commands = Arrays.asList(ImmutableAstCommand.builder().type(AstCommandValue.SET_BODY).value(body).build());
    result.add(ImmutableCreateStoreEntity.builder()
        .bodyType(asset.getType())
        .body(commands)
        .build());
  }

  public void initTag(CreateEntity entity) {
    final var decisions = state.getDecisions().values().stream()
      .map(decision -> {
        final var body = client.mapper().commandsString(optimise.optimise(decision.getSource().getCommands(), AstBodyType.DT));
        return (AstCommand) ImmutableAstCommand.builder()
            .id(decision.getId())
            .value(body)
            .type(AstCommandValue.SET_TAG_DT)
            .build();
      })
      .collect(Collectors.toList());

    // Flow task validations
    final var flowtasks = state.getServices().values().stream()
        .map(init -> {
          final var body = optimise.optimise(init.getSource().getCommands(), AstBodyType.FLOW_TASK).iterator().next().getValue();
          return (AstCommand) ImmutableAstCommand.builder()
              .id(init.getId())
              .value(body)
              .type(AstCommandValue.SET_TAG_ST)
              .build();
        })
        .collect(Collectors.toList());
    
    // Flow validations
    final var flows = state.getFlows().values().stream()
      .map(init -> {
        final var body = optimise.optimise(init.getSource().getCommands(), AstBodyType.FLOW).iterator().next().getValue();
        return (AstCommand) ImmutableAstCommand.builder()
          .id(init.getId())
          .value(body)
          .type(AstCommandValue.SET_TAG_FL)
          .build();
      })

      .collect(Collectors.toList());
    
    final var commands = new ArrayList<AstCommand>();
    commands.add(ImmutableAstCommand.builder()
          .value(LocalDateTime.now().toString())
          .type(AstCommandValue.SET_TAG_CREATED)
          .build());
    commands.add(ImmutableAstCommand.builder()
        .value(entity.getName())
        .type(AstCommandValue.SET_TAG_NAME)
        .build());
    commands.add(ImmutableAstCommand.builder()
        .value(entity.getDesc())
        .type(AstCommandValue.SET_TAG_DESC)
        .build());
    commands.addAll(decisions);
    commands.addAll(flowtasks);
    commands.addAll(flows);

    result.add(ImmutableCreateStoreEntity.builder()
        .bodyType(asset.getType())
        .body(Collections.unmodifiableList(commands))
        .build());
  }
  
  public void initFlowTask(CreateEntity entity) {
    if(!entity.getBody().isEmpty()) {
      result.add(ImmutableCreateStoreEntity.builder()
          .bodyType(asset.getType())
          .body(entity.getBody())
          .build());
    }
    
    final var lnr = System.lineSeparator();
    final var body = new StringBuilder()
        .append("package io.resys.wrench.assets.bundle.groovy;").append(lnr)
        .append("import java.io.Serializable;").append(lnr)
        
        .append("import ").append(DecisionProgram.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(FlowProgram.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(ServiceProgram.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(AstFlow.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(AstDecision.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(AstService.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(DecisionResult.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(DecisionLog.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(FlowResult.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(FlowResultLog.class.getCanonicalName()).append(";").append(lnr)
        
        .append("import ").append(ServiceResult.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(ProgramContext.class.getCanonicalName()).append(";").append(lnr)
        .append("import ").append(ServiceData.class.getCanonicalName()).append(";").append(lnr)
        
        .append("public class ").append(entity.getName()).append(" {").append(lnr)
        .append("  public Output execute(Input input, ProgramContext ctx) {").append(lnr)
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

    final var commands = Arrays.asList(ImmutableAstCommand.builder().type(AstCommandValue.SET_BODY).value(body).build());
    result.add(ImmutableCreateStoreEntity.builder()
        .bodyType(asset.getType())
        .body(commands)
        .build());
  }

  private void initDecision(CreateEntity entity) {
    if(!entity.getBody().isEmpty()) {
      result.add(ImmutableCreateStoreEntity.builder()
          .bodyType(asset.getType())
          .body(entity.getBody())
          .build());
    }
    
    final var commands = Arrays.asList(
      ImmutableAstCommand.builder().type(AstCommandValue.ADD_HEADER_IN).build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_REF).id("0").value("inputColumn").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_TYPE).id("0").value("STRING").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.ADD_HEADER_OUT).build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_REF).id("1").value("outputColumn").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_HEADER_TYPE).id("1").value("STRING").build(),
      ImmutableAstCommand.builder().type(AstCommandValue.SET_NAME).value(entity.getName()).build()
    );

    result.add(ImmutableCreateStoreEntity.builder()
        .bodyType(asset.getType())
        .body(commands)
        .build());
  }
}

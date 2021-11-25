package io.resys.hdes.client.spi.changeset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesClient.HdesTypesMapper;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstBody.Headers;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstDecision.AstDecisionRow;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.spi.decision.DecisionAstBuilderImpl;

public class AstCommandOptimiser {
  private final HdesTypesMapper defs;

  public AstCommandOptimiser(HdesClient client) {
    super();
    this.defs = client.mapper();
  }

  public List<AstCommand> optimise(List<AstCommand> commands, AstBodyType type) {
    switch (type) {
    case DT: return visitDt(commands);
    case FLOW_TASK: return visitSt(commands);
    case FLOW: return visitFl(commands);
    
    default: throw new IllegalArgumentException("unknown type: '" + type + "'");
    }
  }
  
  private List<AstCommand> visitDt(List<AstCommand> src) {
    final var dt = new DecisionAstBuilderImpl(defs).src(src).build();
    final var headers = dt.getHeaders();
    final List<AstCommand> commands = createHeaderCommands(headers);

    createRow(headers, dt.getRows(), commands);
    commands.add(ImmutableAstCommand.builder().value(dt.getHitPolicy().name()).type(AstCommandValue.SET_HIT_POLICY).build());
    commands.add(ImmutableAstCommand.builder().value(dt.getName()).type(AstCommandValue.SET_NAME).build());
    commands.add(ImmutableAstCommand.builder().value(dt.getDescription()).type(AstCommandValue.SET_DESCRIPTION).build());

    return commands;
  }

  private List<AstCommand> visitSt(List<AstCommand> original) {
    final var changes = AstChangesetFactory.src(original, null);
    final var src = new StringBuilder();
    changes.getCommands().forEach(c -> src.append(c.getValue()).append(System.lineSeparator()));
    
    return Arrays.asList(ImmutableAstCommand.builder()
        .value(src.toString())
        .type(AstCommandValue.SET_BODY).build());
  }

  private List<AstCommand> visitFl(List<AstCommand> original) {
    final var changes = AstChangesetFactory.src(original, null);
    final var src = new StringBuilder();
    changes.getCommands().forEach(c -> src.append(c.getValue()).append(System.lineSeparator()));
    
    return Arrays.asList(ImmutableAstCommand.builder()
        .value(src.toString())
        .type(AstCommandValue.SET_BODY).build());
  }

  
  private void createRow(Headers headers, List<AstDecisionRow> nodes, List<AstCommand> result) {  
    final List<TypeDef> types = new ArrayList<>();
    types.addAll(headers.getAcceptDefs());
    types.addAll(headers.getReturnDefs());
    int rows = 1;
    
    for(final var node : nodes) {
    
      int nextId = types.size() * rows + rows;
      result.add(ImmutableAstCommand.builder().type(AstCommandValue.ADD_ROW).build());
  
      Map<String, Object> entries = new HashMap<>();
      node.getCells().forEach(e -> entries.put(e.getHeader(), e.getValue()));
  
      for(final var header : types) {
        Object value = entries.get(header.getName());
        result.add(ImmutableAstCommand.builder()
            .id(String.valueOf(nextId++))
            .value(value == null ? null : header.getSerializer().serialize(header, value))
            .type(AstCommandValue.SET_CELL_VALUE)
            .build());
      }
      
      ++rows;
    }
  }

  private List<AstCommand> createHeaderCommands(Headers headers) {
    final List<AstCommand> result = new ArrayList<>();
    final List<TypeDef> types = new ArrayList<>();
    
    types.addAll(headers.getAcceptDefs());
    types.addAll(headers.getReturnDefs());
    int index = 0;
    for(TypeDef dataType : types) {
      String id = String.valueOf(index);
      result.add(ImmutableAstCommand.builder().type(dataType.getDirection() == Direction.IN ? AstCommandValue.ADD_HEADER_IN : AstCommandValue.ADD_HEADER_OUT).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getName()).type(AstCommandValue.SET_HEADER_REF).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getScript()).type(AstCommandValue.SET_HEADER_SCRIPT).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getValueType() == null ? null : dataType.getValueType().name()).type(AstCommandValue.SET_HEADER_TYPE).build());
      index++;
    }
    return result;
  }
  
  
}

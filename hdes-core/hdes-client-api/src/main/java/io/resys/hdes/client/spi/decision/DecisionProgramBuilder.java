package io.resys.hdes.client.spi.decision;

import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstDecision.Cell;
import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.api.execution.ImmutableDecisionProgram;
import io.resys.hdes.client.api.execution.ImmutableRow;
import io.resys.hdes.client.api.execution.ImmutableRowAccepts;
import io.resys.hdes.client.api.execution.ImmutableRowReturns;
import io.resys.hdes.client.spi.HdesTypeDefsFactory;

public class DecisionProgramBuilder {

  private final HdesTypeDefsFactory typesFactory;
  
  public DecisionProgramBuilder(HdesTypeDefsFactory typesFactory) {
    super();
    this.typesFactory = typesFactory;
  }

  public DecisionProgram build(AstDecision ast) {
    final var program = ImmutableDecisionProgram.builder();
    final var accepts = ast.getHeaders().getAcceptDefs().stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
    final var returns = ast.getHeaders().getReturnDefs().stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
    
    for(var row : ast.getRows()) {
      final var programRow = ImmutableRow.builder().order(row.getOrder());
      for(Cell value : row.getCells()) {
        if(accepts.containsKey(value.getId())) {
          final var typeDef = accepts.get(value.getId());
          programRow.addAccepts(ImmutableRowAccepts.builder()
              .key(typeDef)
              .expression(typesFactory.expression(typeDef.getValueType(), value.getValue()))
              .build());
        } else {
          final var typeDef = returns.get(value.getId());
          programRow.addReturns(ImmutableRowReturns.builder()
              .key(typeDef)
              .value(typeDef.toValue(value.getValue()))
              .build());
        }
      }
      program.addRows(programRow.build());
    }
    return program.build();
  }
}

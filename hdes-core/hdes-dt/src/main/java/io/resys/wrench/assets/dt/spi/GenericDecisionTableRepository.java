package io.resys.wrench.assets.dt.spi;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.execution.DecisionTableResult.DynamicValueExpressionExecutor;
import io.resys.hdes.client.api.execution.DecisionTableResult.NodeExpressionExecutor;
import io.resys.hdes.client.spi.HdesAstTypesImpl;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.spi.builders.CommandDecisionTableBuilder;
import io.resys.wrench.assets.dt.spi.builders.GenericDecisionTableExecutor;
import io.resys.wrench.assets.dt.spi.export.DelegateDecisionTableExporter;

public class GenericDecisionTableRepository implements DecisionTableRepository {

  private final HdesAstTypes dataTypeRepository;
  private final ObjectMapper objectMapper;
  private final NodeExpressionExecutor expressionExecutor;
  private final Supplier<DynamicValueExpressionExecutor> executor;
  private final HdesAstTypes ast;
  public GenericDecisionTableRepository(
      ObjectMapper objectMapper,
      HdesAstTypes dataTypeRepository,
      NodeExpressionExecutor expressionExecutor,
      Supplier<DynamicValueExpressionExecutor> executor) {
    super();
    this.objectMapper = objectMapper;
    this.dataTypeRepository = dataTypeRepository;
    this.expressionExecutor = expressionExecutor;
    this.executor = executor;
    this.ast = new HdesAstTypesImpl(objectMapper);

  }

  @Override
  public DecisionTableBuilder createBuilder() {
    return new CommandDecisionTableBuilder(objectMapper, executor.get(), dataTypeRepository, () -> ast);
  }
  @Override
  public DecisionTableExecutor createExecutor() {
    return new GenericDecisionTableExecutor(expressionExecutor);
  }
  @Override
  public DecisionTableExporter createExporter() {
    return new DelegateDecisionTableExporter(objectMapper);
  }
}

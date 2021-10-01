package io.resys.wrench.assets.dt.spi;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.DecisionAstType.ColumnExpressionType;
import io.resys.wrench.assets.datatype.api.DataTypeRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.spi.builders.CommandDecisionTableBuilder;
import io.resys.wrench.assets.dt.spi.builders.GenericDecisionTableCommandModelBuilder;
import io.resys.wrench.assets.dt.spi.builders.GenericDecisionTableExecutor;
import io.resys.wrench.assets.dt.spi.export.DelegateDecisionTableExporter;

public class GenericDecisionTableRepository implements DecisionTableRepository {

  private final DataTypeRepository dataTypeRepository;
  private final ObjectMapper objectMapper;
  private final NodeExpressionExecutor expressionExecutor;
  private final Supplier<DynamicValueExpressionExecutor> executor;
  private final Supplier<List<String>> headerTypes;
  private final Supplier<DecisionTableExpressionBuilder> expressionBuilder;
  private final Map<ValueType, List<String>> headerExpressions;

  public GenericDecisionTableRepository(
      ObjectMapper objectMapper,
      DataTypeRepository dataTypeRepository,
      NodeExpressionExecutor expressionExecutor,
      Supplier<DynamicValueExpressionExecutor> executor,
      Supplier<DecisionTableExpressionBuilder> expressionBuilder) {
    super();
    this.objectMapper = objectMapper;
    this.dataTypeRepository = dataTypeRepository;
    this.expressionExecutor = expressionExecutor;
    this.expressionBuilder = expressionBuilder;
    this.executor = executor;
    this.headerTypes = () -> Collections.unmodifiableList(
        Arrays.asList(ValueType.STRING,  ValueType.BOOLEAN, ValueType.INTEGER, ValueType.LONG, ValueType.DECIMAL, ValueType.DATE, ValueType.DATE_TIME).stream()
        .map(v -> v.name())
        .collect(Collectors.toList()));

    Map<ValueType, List<String>> headerExpressions = new HashMap<>();
    headerExpressions.put(ValueType.INTEGER, Collections.unmodifiableList(Arrays.asList(ColumnExpressionType.EQUALS.name())));
    headerExpressions.put(ValueType.DECIMAL, Collections.unmodifiableList(Arrays.asList(ColumnExpressionType.EQUALS.name())));
    headerExpressions.put(ValueType.STRING, Collections.unmodifiableList(Arrays.asList(ColumnExpressionType.IN.name())));
    this.headerExpressions = Collections.unmodifiableMap(headerExpressions);
  }

  @Override
  public DecisionTableBuilder createBuilder() {
    return new CommandDecisionTableBuilder(objectMapper, executor.get(), dataTypeRepository, () -> createCommandModelBuilder());
  }
  @Override
  public DecisionTableExecutor createExecutor() {
    return new GenericDecisionTableExecutor(expressionExecutor);
  }
  @Override
  public DecisionTableExporter createExporter() {
    return new DelegateDecisionTableExporter(objectMapper);
  }
  @Override
  public DecisionTableCommandModelBuilder createCommandModelBuilder() {
    return new GenericDecisionTableCommandModelBuilder(headerTypes, expressionBuilder, headerExpressions, executor);
  }
  @Override
  public DecisionTableExpressionBuilder createExpression() {
    return expressionBuilder.get();
  }
}

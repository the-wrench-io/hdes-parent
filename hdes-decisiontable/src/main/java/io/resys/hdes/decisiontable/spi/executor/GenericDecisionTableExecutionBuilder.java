package io.resys.hdes.decisiontable.spi.executor;

/*-
 * #%L
 * hdes-decisiontable
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableExecution;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.api.ImmutableDecisionTableExecution;
import io.resys.hdes.execution.HdesService;
import io.resys.hdes.execution.ImmutableExecution;


public class GenericDecisionTableExecutionBuilder implements DecisionTableService.ExecutionBuilder {

  private final DataTypeService dataTypeService;
  private DecisionTableAst decisionTable;
  private DataTypeInput input;

  public GenericDecisionTableExecutionBuilder(DataTypeService dataTypeService) {
    super();
    this.dataTypeService = dataTypeService;
  }

  @Override
  public GenericDecisionTableExecutionBuilder ast(DecisionTableAst decisionTable) {
    this.decisionTable = decisionTable;
    return this;
  }

  @Override
  public GenericDecisionTableExecutionBuilder input(DataTypeInput context) {
    this.input = context;
    return this;
  }

  @Override
  public Single<HdesService.Execution<DecisionTableExecution>> build() {
    Assert.notNull(decisionTable, () -> "ast can't be null!");
    Assert.notNull(input, () -> "input can't be null!");
    return Single.create(emitter -> {
          DecisionTableAst.Node node = decisionTable.getNode();
          List<DecisionTableExecution> values = new ArrayList<>();
          
          if(decisionTable.getHitPolicy() == DecisionTableModel.HitPolicy.FIRST) {
            executeHitPolicyFirst(values, node);
          } else {
            executeHitPolicyMatchAll(values, node);
          }
          ImmutableExecution.Builder<DecisionTableExecution> builder = ImmutableExecution.builder();
          emitter.onSuccess(builder
              .label("dt")
              .tag("latest")
              .name(decisionTable.getId())
              .id(UUID.randomUUID().toString())
              .localDateTime(LocalDateTime.now())
              .addAllTypes(decisionTable.getTypes().stream().map(t -> t.getValue()).collect(Collectors.toList()))
              .addAllValue(values)
              .build());
        });
  }

  private void executeHitPolicyMatchAll(List<DecisionTableExecution> emitter, DecisionTableAst.Node node) {
    if(node != null) {
      DecisionTableExecution decision = evaluateNode(node);
      if(decision != null) {
        emitter.add(decision);
      }
      executeHitPolicyMatchAll(emitter, node.getNext());
    }
  }
  private void executeHitPolicyFirst(List<DecisionTableExecution> emitter, DecisionTableAst.Node node) {
    if(node != null) {
      DecisionTableExecution decision = evaluateNode(node);
      if(decision != null) {
        emitter.add(decision);
        return;
      }
      executeHitPolicyFirst(emitter, node.getNext());
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private DecisionTableExecution evaluateNode(DecisionTableAst.Node node) {
    Boolean match = node.getInputs().isEmpty();
    Map<String, Serializable> inputEntities = new HashMap<>();
    for(Map.Entry<DataType, DataTypeService.Expression> entry : node.getInputs().entrySet()) {

      DataType dataType = entry.getKey();
      Object rawEntity = this.input.apply(dataType);
      
      final Serializable entity = dataTypeService.mapper(dataType.getValueType()).toValue(rawEntity, dataType);
      inputEntities.put(dataType.getName(), entity);

      DataTypeService.Expression expression = entry.getValue();
      DataTypeService.Operation operation = expression.getOperation();
      match = (boolean) operation.apply(entity);
      if(!match) {
        break;
      }
    }

    if(!Boolean.TRUE.equals(match) ) {
      return null;
    }
    
    return ImmutableDecisionTableExecution.builder()
            .inputs(inputEntities)
            .node(node)
            .build();
  }
}

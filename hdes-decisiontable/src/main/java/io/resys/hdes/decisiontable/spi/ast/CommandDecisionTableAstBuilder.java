package io.resys.hdes.decisiontable.spi.ast;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableAst.RuleType;
import io.resys.hdes.decisiontable.api.DecisionTableFlatModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Row;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.api.DecisionTableService.AstBuilder;
import io.resys.hdes.decisiontable.api.ImmutableDecisionTableAst;
import io.resys.hdes.decisiontable.api.ImmutableRuleType;
import io.resys.hdes.decisiontable.spi.ast.beans.DecisionTableAstNodeBean;

public class CommandDecisionTableAstBuilder implements DecisionTableService.AstBuilder {
  private final DataTypeService dataTypeService;
  private DecisionTableModel model;

  public CommandDecisionTableAstBuilder(DataTypeService dataTypeService) {
    super();
    this.dataTypeService = dataTypeService;
  }

  @Override
  public AstBuilder from(DecisionTableModel model) {
    this.model = model;
    return this;
  }

  @Override
  public AstBuilder from(DecisionTableFlatModel model) {
    return this;
  }

  @Override
  public DecisionTableAst build() {
    Assert.notNull(model, () -> "model can't be null!");
    List<RuleType> types = createTypes(model);
    Map<Integer, DataType> typesById = types.stream().collect(Collectors.toMap(t -> t.getOrder(), t -> t.getValue()));
    DecisionTableAstNodeBean first = null;
    DecisionTableAstNodeBean previous = null;
    for (Row row : model.getRows()) {
      int id = previous == null ? 0 : previous.getId() + 1;
      DecisionTableAstNodeBean current = new DecisionTableAstNodeBean(
          id, row.getOrder(),
          getInputs(typesById, row),
          getOutputs(typesById, row),
          previous);
      if (first == null) {
        first = current;
      }
      if (previous != null) {
        previous.setNext(current);
      }
      previous = current;
    }
    return ImmutableDecisionTableAst
        .builder()
        .id(model.getName())
        .hitPolicy(model.getHitPolicy())
        .types(types)
        .node(first)
        .build();
  }

  protected List<RuleType> createTypes(DecisionTableModel data) {
    List<RuleType> result = new ArrayList<>();
    int index = 0;
    for (Header header : data.getHeaders()) {
      result.add(
          ImmutableRuleType.builder()
              .order(index++)
              .value(resolveType(header.getValue(), header.getName(), header.getDirection()))
              .build());
    }
    Collections.sort(result, (o1, o2) ->  Integer.compare(o1.getOrder(), o2.getOrder()));
    return Collections.unmodifiableList(result);
  }

  protected Map<DataType, DataTypeService.Expression> getInputs(Map<Integer, DataType> typesById, Row entry) {
    Map<DataType, DataTypeService.Expression> result = new HashMap<>();
    int index = 0;
    for (Cell value : entry.getCells()) {
      DataType type = typesById.get(index++);
      if (type.getDirection() == Direction.IN) {
        result.put(type, 
            dataTypeService.expression()
              .src(value.getValue())
              .valueType(type.getValueType())
              .build()
            );
      }
    }
    return Collections.unmodifiableMap(result);
  }

  protected Map<DataType, Serializable> getOutputs(Map<Integer, DataType> typesById, Row entry) {
    Map<DataType, Serializable> result = new HashMap<>();
    int index = 0;
    for (Cell value : entry.getCells()) {
      DataType type = typesById.get(index++);
      if (type.getDirection() == Direction.OUT) {
        final Serializable cellValue = dataTypeService.mapper(type.getValueType()).toValue(value.getValue(), type);
        result.put(type, cellValue);
      }
    }
    return Collections.unmodifiableMap(result);
  }

  protected DataType resolveType(String value, String name, Direction direction) {
    ValueType valueType = value != null ? ValueType.valueOf(value) : null;
    return dataTypeService.model().name(name).valueType(valueType).direction(direction).build();
  }
}

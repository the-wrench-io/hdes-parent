package io.resys.hdes.decisiontable.spi.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableFlatModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.HitPolicy;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.api.ImmutableDecisionTableAst;
import io.resys.hdes.decisiontable.api.ImmutableRuleType;
import io.resys.hdes.decisiontable.spi.ast.beans.DecisionTableAstNodeBean;


public class FlatDecisionTableAstBuilder implements DecisionTableService.AstBuilder {

  private final DataTypeService dataTypeService;
  private DecisionTableFlatModel model;

  public FlatDecisionTableAstBuilder(DataTypeService dataTypeService) {
    super();
    this.dataTypeService = dataTypeService;
  }

  @Override
  public DecisionTableService.AstBuilder from(DecisionTableModel model) {
    return this;
  }

  @Override
  public DecisionTableService.AstBuilder from(DecisionTableFlatModel model) {
    this.model = model;
    return this;
  }

  @Override
  public DecisionTableAst build() {
    DecisionTableFlatModel data = this.model;
    List<DecisionTableAst.RuleType> types = createTypes(data);
    Map<Integer, DataType> typesById = types.stream().collect(Collectors.toMap(t -> t.getOrder(), t -> t.getValue()));

    DecisionTableAstNodeBean first = null;
    DecisionTableAstNodeBean previous = null;
    int index = 0;
    for(DecisionTableFlatModel.Entry row : data.getEntries()) {
      
      row.setId(index++);
      cleanEntry(row);
      assertEntry(row, data.getTypes());
      
      
      int id = previous == null ? 0 : previous.getId() + 1;
      DecisionTableAstNodeBean current = new DecisionTableAstNodeBean(
          id, row.getId(), 
          getInputs(typesById, row), getOutputs(typesById, row), previous);
      if(first == null) {
        first = current;
      }
      if(previous != null) {
        previous.setNext(current);
      }
      previous = current;
    }

    HitPolicy hitPoicy = data.getHitPolicy() == null ? HitPolicy.FIRST : data.getHitPolicy();
    return ImmutableDecisionTableAst.builder()
        .id(data.getName())
        .hitPolicy(hitPoicy)
        .types(types)
        .node(first).build();
  }

  private List<DecisionTableAst.RuleType> createTypes(DecisionTableFlatModel data) {
    List<DecisionTableAst.RuleType> result = new ArrayList<>();
    int index = 0;
    for(DecisionTableFlatModel.Type t : data.getTypes()) {
      result.add(
          ImmutableRuleType.builder()
            .order(index++)
            .value(resolveType(t.getType(), t.getName(), t.getDirection()))
            .build());
    }
    
    Collections.sort(result, (o1, o2) ->  Integer.compare(o1.getOrder(), o2.getOrder()));
    return Collections.unmodifiableList(result);
  }

  private Map<DataType, DataTypeService.Expression> getInputs(Map<Integer, DataType> typesById, DecisionTableFlatModel.Entry entry) {
    Map<DataType, DataTypeService.Expression> result = new HashMap<>();
    for(DecisionTableFlatModel.Value value : entry.getValues()) {
      DataType type = typesById.get(value.getId());
      if(type.getDirection() == DataType.Direction.IN) {
        result.put(type, dataTypeService.expression().valueType(type.getValueType()).src(value.getValue()).build());
      }

    }
    return Collections.unmodifiableMap(result);
  }

  private Map<DataType, Serializable> getOutputs(Map<Integer, DataType> typesById, DecisionTableFlatModel.Entry entry) {
    Map<DataType, Serializable> result = new HashMap<>();
    for(DecisionTableFlatModel.Value value : entry.getValues()) {
      DataType type = typesById.get(value.getId());
      if(type.getDirection() == DataType.Direction.OUT) {
        
        final Serializable cellValue = dataTypeService.mapper(type.getValueType()).toValue(value.getValue(), type);
        result.put(type, cellValue);
      }
    }
    return Collections.unmodifiableMap(result);
  }

  private void cleanEntry(DecisionTableFlatModel.Entry entry) {
    Iterator<DecisionTableFlatModel.Value> iterator = entry.getValues().iterator();
    while(iterator.hasNext()) {
      DecisionTableFlatModel.Value value = iterator.next();
      if(value.getValue() != null) {
        value.setValue(value.getValue().trim());
      }
      if(StringUtils.isEmpty(value.getValue())) {
        iterator.remove();
      }
    }
  }

  private void assertEntry(DecisionTableFlatModel.Entry entry, List<DecisionTableFlatModel.Type> types) {
    Iterator<DecisionTableFlatModel.Value> iterator = entry.getValues().iterator();
    while(iterator.hasNext()) {
      DecisionTableFlatModel.Value value = iterator.next();
      if(StringUtils.isEmpty(value.getValue())) {
        iterator.remove();
        continue;
      }
      long id = value.getId();
      Assert.isTrue(id >= 0 && id < types.size(), () -> "Index: \"" + entry.getId() + "\", value: \"" + value.getId() +   "\" is undefined in types!");
    }
  }

  private DataType resolveType(String value, String name, DataType.Direction direction) {
    DataType.ValueType valueType = value != null ? DataType.ValueType.valueOf(value) : null;
    return dataTypeService.model().
      name(name).
      valueType(valueType).
      direction(direction).
      build();
  }
}

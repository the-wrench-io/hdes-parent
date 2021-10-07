package io.resys.wrench.assets.flow.spi.support;

/*-
 * #%L
 * wrench-assets-flow
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.AstDataType.Direction;
import io.resys.hdes.client.api.ast.AstDataType.ValueType;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstInput;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstNode;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstTask;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskType;
import io.resys.wrench.assets.flow.spi.FlowDefinitionException;

public class NodeFlowAdapter {

  public static String getStringValue(FlowAstNode node) {
    if (node == null || node.getValue() == null) {
      return null;
    }
    return node.getValue();
  }

  public static boolean getBooleanValue(FlowAstNode node) {
    if (node == null || node.getValue() == null) {
      return false;
    }
    return Boolean.parseBoolean(node.getValue());
  }

  public static Collection<AstDataType> getInputs(NodeFlow data, HdesAstTypes dataTypeRepository) {
    Map<String, FlowAstInput> inputs = data.getInputs();

    int index = 0;
    Collection<AstDataType> result = new ArrayList<>();
    for (Map.Entry<String, FlowAstInput> entry : inputs.entrySet()) {
      if (entry.getValue().getType() == null) {
        continue;
      }
      try {
        ValueType valueType = ValueType.valueOf(entry.getValue().getType().getValue());
        boolean required = getBooleanValue(entry.getValue().getRequired());
        result.add(dataTypeRepository.dataType()
            .id(entry.getValue().getStart() + "")
            .order(index++)
            .name(entry.getKey()).valueType(valueType).direction(Direction.IN).required(required)
            .values(getStringValue(entry.getValue().getDebugValue()))
            .build());
        
      } catch (Exception e) {
        final String msg = String.format("Failed to convert data type from: %s, error: %s", entry.getValue().getType().getValue(), e.getMessage());
        throw new FlowDefinitionException(msg, e);
      }
    }
    return Collections.unmodifiableCollection(result);
  }

  public static FlowTaskType getTaskType(FlowAstTask task) {
    if (task.getUserTask() != null) {
      return FlowTaskType.USER_TASK;
    } else if (task.getDecisionTable() != null) {
      return FlowTaskType.DT;
    } else if (task.getService() != null) {
      return FlowTaskType.SERVICE;
    }

    return FlowTaskType.EMPTY;
  }
}

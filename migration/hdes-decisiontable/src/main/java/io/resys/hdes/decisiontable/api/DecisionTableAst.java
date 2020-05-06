package io.resys.hdes.decisiontable.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

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
import io.resys.hdes.decisiontable.api.DecisionTableModel.HitPolicy;

@Value.Immutable
public interface DecisionTableAst extends Serializable {
  String getId();
  HitPolicy getHitPolicy();
  List<RuleType> getTypes();
  Node getNode();

  @Value.Immutable
  interface RuleType extends Serializable {
    int getOrder();
    DataType getValue();
  }

  interface Node extends Serializable {
    int getId();
    int getOrder();
    Map<DataType, DataTypeService.Expression> getInputs();
    Map<DataType, Serializable> getOutputs();
    Node getPrevious();
    Node getNext();
  }
  

}

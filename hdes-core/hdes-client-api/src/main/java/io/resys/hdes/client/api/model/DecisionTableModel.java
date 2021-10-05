package io.resys.hdes.client.api.model;

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

import java.io.Serializable;
import java.util.List;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.DecisionAstType.HitPolicy;

public interface DecisionTableModel extends Model {
  String getId();
  String getDescription();
  String getRev();
  String getSrc();
  HitPolicy getHitPolicy();
  List<DecisionTableDataType> getTypes();
  DecisionTableNode getNode();

  interface DecisionTableDataType extends Serializable, Comparable<DecisionTableDataType> {
    int getOrder();
    String getScript();
    AstDataType getValue();
  }

  interface DecisionTableNode extends Serializable {
    int getId();
    int getOrder();
    
    List<DecisionTableNodeInput> getInputs();
    List<DecisionTableNodeOutput> getOutputs();
    
    DecisionTableNode getPrevious();
    DecisionTableNode getNext();
  }

  
  interface DecisionTableNodeInput extends Serializable {
    AstDataType getKey();
    String getValue();
  }
  
  interface DecisionTableNodeOutput extends Serializable {
    AstDataType getKey();
    Serializable getValue();
  }
  
  
}

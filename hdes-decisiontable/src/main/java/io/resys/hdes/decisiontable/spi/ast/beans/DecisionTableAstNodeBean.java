package io.resys.hdes.decisiontable.spi.ast.beans;

import java.io.Serializable;
import java.util.Map;

/*-
 * #%L
 * wrench-component-dt
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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
import io.resys.hdes.decisiontable.api.DecisionTableAst;

public class DecisionTableAstNodeBean implements DecisionTableAst.Node {

  private static final long serialVersionUID = 6182243429408952190L;

  private final int id;
  private final int order;
  private final Map<DataType, DataTypeService.Expression> inputs;
  private final Map<DataType, Serializable> outputs;
  private final DecisionTableAst.Node previous;
  private DecisionTableAstNodeBean next;

  public DecisionTableAstNodeBean(
      int id, int order, 
      Map<DataType, DataTypeService.Expression> inputs, 
      Map<DataType, Serializable> outputs, 
      DecisionTableAstNodeBean previous) {
    super();
    this.order = order;
    this.id = id;
    this.inputs = inputs;
    this.outputs = outputs;
    this.previous = previous;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public Map<DataType, DataTypeService.Expression> getInputs() {
    return inputs;
  }

  @Override
  public Map<DataType, Serializable> getOutputs() {
    return outputs;
  }

  @Override
  public DecisionTableAst.Node getPrevious() {
    return previous;
  }

  @Override
  public DecisionTableAstNodeBean getNext() {
    return next;
  }
  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
    result = prime * result + ((next == null) ? 0 : next.hashCode());
    result = prime * result + order;
    result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DecisionTableAstNodeBean other = (DecisionTableAstNodeBean) obj;
    if (id != other.id)
      return false;
    if (inputs == null) {
      if (other.inputs != null)
        return false;
    } else if (!inputs.equals(other.inputs))
      return false;
    if (next == null) {
      if (other.next != null)
        return false;
    } else if (!next.equals(other.next))
      return false;
    if (order != other.order)
      return false;
    if (outputs == null) {
      if (other.outputs != null)
        return false;
    } else if (!outputs.equals(other.outputs))
      return false;
    return true;
  }

  public void setNext(DecisionTableAstNodeBean next) {
    this.next = next;
  }
}

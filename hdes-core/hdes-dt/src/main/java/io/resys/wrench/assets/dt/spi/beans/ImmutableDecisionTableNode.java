package io.resys.wrench.assets.dt.spi.beans;

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

import java.io.Serializable;
import java.util.Map;

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNode;

public class ImmutableDecisionTableNode implements DecisionTableNode {

  private static final long serialVersionUID = 6182243429408952190L;

  private final int id;
  private final int order;
  private final Map<DataType, String> inputs;
  private final Map<DataType, Serializable> outputs;
  private final DecisionTableNode previous;
  private DecisionTableNode next;

  public ImmutableDecisionTableNode(
      int id, int order, Map<DataType, String> inputs, Map<DataType, Serializable> outputs, DecisionTableNode previous) {
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
  public Map<DataType, String> getInputs() {
    return inputs;
  }

  @Override
  public Map<DataType, Serializable> getOutputs() {
    return outputs;
  }

  @Override
  public DecisionTableNode getPrevious() {
    return previous;
  }

  @Override
  public DecisionTableNode getNext() {
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
    ImmutableDecisionTableNode other = (ImmutableDecisionTableNode) obj;
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

  public void setNext(DecisionTableNode next) {
    this.next = next;
  }
}

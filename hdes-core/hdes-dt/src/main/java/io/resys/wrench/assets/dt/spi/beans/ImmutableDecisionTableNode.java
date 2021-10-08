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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.execution.DecisionProgram.Row;
import io.resys.hdes.client.api.execution.DecisionProgram.RowAccepts;
import io.resys.hdes.client.api.execution.DecisionProgram.RowReturns;

public class ImmutableDecisionTableNode implements Row {

  private static final long serialVersionUID = 6182243429408952190L;

  private final int id;
  private final int order;
  private final List<RowAccepts> inputs;
  private final List<RowReturns> outputs;
  @JsonIgnore
  private final Row previous;
  private Row next;

  public ImmutableDecisionTableNode(
      int id, int order, Map<TypeDef, String> inputs, Map<TypeDef, Serializable> outputs, Row previous) {
    super();
    this.order = order;
    this.id = id;
    this.inputs = inputs.entrySet().stream().map(e -> new ImmutableDecisionTableNodeInput(e.getKey(), e.getValue())).collect(Collectors.toUnmodifiableList());
    this.outputs = outputs.entrySet().stream().map(e -> new ImmutableDecisionTableNodeOutput(e.getKey(), e.getValue())).collect(Collectors.toUnmodifiableList());
    this.previous = previous;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public List<RowAccepts> getAccepts() {
    return inputs;
  }

  @Override
  public List<RowReturns> getReturns() {
    return outputs;
  }

  @Override
  public Row getPrevious() {
    return previous;
  }

  @Override
  public Row getNext() {
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

  public void setNext(Row next) {
    this.next = next;
  }
  
  private static final class ImmutableDecisionTableNodeOutput implements RowReturns {
    private static final long serialVersionUID = 7364145982894215127L;
    private final TypeDef key;
    private final Serializable value;
    public ImmutableDecisionTableNodeOutput(TypeDef key, Serializable value) {
      super();
      this.key = key;
      this.value = value;
    }
    public TypeDef getKey() {
      return key;
    }
    public Serializable getValue() {
      return value;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
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
      ImmutableDecisionTableNodeOutput other = (ImmutableDecisionTableNodeOutput) obj;
      if (key == null) {
        if (other.key != null)
          return false;
      } else if (!key.equals(other.key))
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      return true;
    }
  }
  
  private static final class ImmutableDecisionTableNodeInput implements RowAccepts {
    private static final long serialVersionUID = -6095582758590972380L;
    private final TypeDef key;
    private final String value;
    public ImmutableDecisionTableNodeInput(TypeDef key, String value) {
      super();
      this.key = key;
      this.value = value;
    }
    public TypeDef getKey() {
      return key;
    }
    public String getExpression() {
      return value;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
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
      ImmutableDecisionTableNodeInput other = (ImmutableDecisionTableNodeInput) obj;
      if (key == null) {
        if (other.key != null)
          return false;
      } else if (!key.equals(other.key))
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      return true;
    }
  }
}

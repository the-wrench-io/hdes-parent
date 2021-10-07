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

import io.resys.hdes.client.api.ast.AstBody.AstExpression;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableOutput;

public class ImmutableDecisionTableOutput implements DecisionTableOutput {

  private static final long serialVersionUID = -762747003682900356L;

  private final int id;
  private final int order;
  private final Map<String, Serializable> values;
  private final Map<String, AstExpression> expressions;
  
  public ImmutableDecisionTableOutput(
      int id, int order, Map<String, Serializable> values,
      Map<String, AstExpression> expressions) {
    super();
    this.id = id;
    this.order = order;
    this.values = values;
    this.expressions = expressions;
  }

  @Override
  public Map<String, AstExpression> getExpressions() {
    return expressions;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public Map<String, Serializable> getValues() {
    return values;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + order;
    result = prime * result + ((values == null) ? 0 : values.hashCode());
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
    ImmutableDecisionTableOutput other = (ImmutableDecisionTableOutput) obj;
    if (id != other.id)
      return false;
    if (order != other.order)
      return false;
    if (values == null) {
      if (other.values != null)
        return false;
    } else if (!values.equals(other.values))
      return false;
    return true;
  }
}

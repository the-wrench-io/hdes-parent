package io.resys.wrench.assets.dt.spi.beans;

/*-
 * #%L
 * wrench-assets-dt
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

import java.util.List;

import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableExpression;
import io.resys.wrench.assets.dt.spi.expression.Operation;


@SuppressWarnings("rawtypes")
public class ImmutableDecisionTableExpression implements DecisionTableExpression {
  private final transient Operation expression;
  private final String src;
  private final ValueType type;
  private final List<String> constants;

  public ImmutableDecisionTableExpression(
      Operation expression,
      String src, ValueType type, List<String> constants) {
    super();
    this.expression = expression;
    this.src = src;
    this.type = type;
    this.constants = constants;
  }

  @Override
  public String getSrc() {
    return src;
  }
  @Override
  public ValueType getType() {
    return type;
  }
  @Override
  public List<String> getConstants() {
    return constants;
  }
  @SuppressWarnings("unchecked")
  @Override
  public Object getValue(Object entity) {
    return expression.apply(entity);
  }
}

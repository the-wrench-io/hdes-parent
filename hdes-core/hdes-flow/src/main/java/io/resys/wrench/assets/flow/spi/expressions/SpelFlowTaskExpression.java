package io.resys.wrench.assets.flow.spi.expressions;

/*-
 * #%L
 * wrench-component-flow
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

import java.util.Collections;
import java.util.List;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import io.resys.hdes.client.api.model.FlowModel.FlowTaskExpression;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskExpressionContext;

public class SpelFlowTaskExpression implements FlowTaskExpression {

  private static final long serialVersionUID = -5237729310088388943L;

  private final static transient MapPropertyAccessor mapAccessor = new MapPropertyAccessor();
  private final static transient FlowTaskExpressionContextPropertyAccessor contextAccessor = new FlowTaskExpressionContextPropertyAccessor();
  private final static transient ExpressionParser parser = new SpelExpressionParser();

  private final String value;
  private final List<String> inputs;
  private final transient SpelExpression exp;

  public SpelFlowTaskExpression(String value, List<String> inputs) {
    this.exp = value == null ? null : (SpelExpression) parser.parseExpression(value);
    this.value = value;
    this.inputs = Collections.unmodifiableList(inputs);
  }

  @Override
  public String getValue() {
    return value;
  }
  @Override
  public boolean eval(FlowTaskExpressionContext context) {
    if(exp == null) {
      return true;
    }
    StandardEvaluationContext evalContext = new StandardEvaluationContext(context);
    evalContext.addPropertyAccessor(mapAccessor);
    evalContext.addPropertyAccessor(contextAccessor);
    return (boolean) exp.getValue(evalContext);
  }
  @Override
  public List<String> getInputs() {
    return inputs;
  }
}

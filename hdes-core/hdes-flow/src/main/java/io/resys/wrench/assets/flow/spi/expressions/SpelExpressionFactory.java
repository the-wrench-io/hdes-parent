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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import io.resys.hdes.client.api.model.ImmutableFlowTaskValue;

public class SpelExpressionFactory implements ExpressionFactory {

  private final static transient ExpressionParser parser = new SpelExpressionParser();

  @Override
  public ImmutableFlowTaskValue get(String expression) {
    SpelExpression exp = (SpelExpression) parser.parseExpression(expression);
    SpelNode node = exp.getAST();
    List<String> inputs = new ArrayList<>();
    getInputs(node, inputs);

    SpelFlowTaskExpression spelFlowTaskExpression = new SpelFlowTaskExpression(expression, inputs);
    Map<String, String> inputMapping = new HashMap<>();
    inputs.forEach(name -> inputMapping.put(name, name));

    return ImmutableFlowTaskValue.builder()
        .expression(spelFlowTaskExpression)
        .isCollection(false)
        .inputs(inputMapping)
        .build();
  }


  protected void getInputs(SpelNode node, List<String> inputs) {
    if(node instanceof CompoundExpression) {
      CompoundExpression ref = (CompoundExpression) node;
      inputs.add(ref.toStringAST());
      return;
    } else if(node instanceof PropertyOrFieldReference) {
      PropertyOrFieldReference ref = (PropertyOrFieldReference) node;
      inputs.add(ref.getName());
    }

    for(int index = 0; index < node.getChildCount(); index++) {
      getInputs(node.getChild(index), inputs);
    }
  }
}

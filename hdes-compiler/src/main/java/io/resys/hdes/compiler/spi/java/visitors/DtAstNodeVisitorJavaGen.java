package io.resys.hdes.compiler.spi.java.visitors;

/*-
 * #%L
 * hdes-compiler
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.awt.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DirectionType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.ExpressionValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Header;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicy;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.LiteralValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtCodeSpec;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtCodeSpecPair;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtMethodSpec;

public class DtAstNodeVisitorJavaGen extends DtAstNodeVisitorTemplate<DtJavaSpec, TypeSpec> {
  private DecisionTableBody body;
  private ClassName input;
  private ClassName output;
  
  @Override
  public TypeSpec visitDecisionTableBody(DecisionTableBody node) {
    this.body = node;
    this.input = ClassName.get("", JavaNaming.dtInput(body.getId()));
    this.output = ClassName.get("", JavaNaming.dtOutput(body.getId()));
    
    return TypeSpec.classBuilder(JavaNaming.dtImpl(node.getId()))
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ClassName.get("", node.getId()))
        .addJavadoc(node.getDescription().orElse(""))
        .addMethod(visitHitPolicy(node.getHitPolicy()).getValue())
        .build();
  }

  private DtMethodSpec visitHitPolicy(HitPolicy node) {
    if (node instanceof HitPolicyAll) {
      return visitHitPolicyAll((HitPolicyAll) node);
    } else if (node instanceof HitPolicyMatrix) {
      return visitHitPolicyMatrix((HitPolicyMatrix) node);
    }
    return visitHitPolicyFirst((HitPolicyFirst) node);
  }

  @Override
  public DtMethodSpec visitHitPolicyAll(HitPolicyAll node) {

    ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(List.class), output);
    CodeBlock.Builder statements = CodeBlock.builder()
        .addStatement("$T result = new $T<>()", returnType, ArrayList.class);
    for (RuleRow row : node.getRows()) {
      DtCodeSpecPair pair = visitRuleRow(row);
      statements.beginControlFlow("if($L)", pair.getKey());
      statements.addStatement("result.add($L)", pair.getValue());
      statements.endControlFlow();
    }
    return ImmutableDtMethodSpec.builder().value(
        MethodSpec.methodBuilder("apply")
          .addAnnotation(Override.class)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(ParameterSpec.builder(input, "input").build())
          .returns(returnType)
          .addCode(statements.addStatement("return result").build())
          .build()).build();
  }

  @Override
  public DtCodeSpecPair visitRuleRow(RuleRow node) {
    CodeBlock.Builder key = CodeBlock.builder();
    CodeBlock.Builder value = CodeBlock.builder().add("Immutable$T.builder()", output);
    boolean and = false;
    for (Rule rule : node.getRules()) {
      Header header = body.getHeaders().getValues().get(rule.getHeader());
      CodeBlock ruleCode = visitRule(rule).getValue();
      if(ruleCode.isEmpty()) {
        continue;
      }
      
      if (header.getDirection() == DirectionType.IN) {
        if (and) {
          key.add(" && ");
        }
        key.add(ruleCode);
        and = true;
      } else {
        value.add(ruleCode);
      }
    }
    return ImmutableDtCodeSpecPair.builder()
        .key(key.build()).value(value.add(".build()").build())
        .build();
  }

  @Override
  public DtCodeSpec visitRule(Rule node) {
    RuleValue value = node.getValue();
    if (value instanceof UndefinedValue) {
      return ImmutableDtCodeSpec.builder().value(CodeBlock.builder().build()).build();
    }
    DtCodeSpec expression;
    if (value instanceof LiteralValue) {
      expression = visitLiteral(((LiteralValue) value).getValue());
    } else {
      expression = visitExpressionValue((ExpressionValue) value);
    }
    Header header = body.getHeaders().getValues().get(node.getHeader());
    if (header.getDirection() == DirectionType.IN) {
      return expression;
    }
    return ImmutableDtCodeSpec.builder()
        .value(CodeBlock.builder()
            .add(".").add(header.getName())
            .add("($L)", expression.getValue())
            .build())
        .build();
  }

  @Override
  public DtCodeSpec visitExpressionValue(ExpressionValue node) {
    return ImmutableDtCodeSpec.builder().value(
        CodeBlock.builder()
            .build())
        .build();
  }

  @Override
  public DtCodeSpec visitLiteral(Literal node) {
    CodeBlock.Builder code = CodeBlock.builder();
    
    if(node.getType() == ScalarType.DECIMAL) {
      code.add("new $T(\"$L\")", BigDecimal.class, node.getValue());
    } else if(node.getType() == ScalarType.DATE) {
      code.add("$T.parse($L)", LocalDate.class, node.getValue());
    } else if(node.getType() == ScalarType.DATE_TIME) {
      code.add("$T.parse($L)", LocalDateTime.class, node.getValue());
    } else if(node.getType() == ScalarType.TIME) {
      code.add("$T.parse($L)", LocalTime.class, node.getValue());
    } else if(node.getType() == ScalarType.STRING) {
      code.add("$S", node.getValue());
    } else {
      code.add(node.getValue());
    }
    
    return ImmutableDtCodeSpec.builder().value(code.build()).build();
  }

  @Override
  public DtMethodSpec visitHitPolicyFirst(HitPolicyFirst node) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public DtMethodSpec visitHitPolicyMatrix(HitPolicyMatrix node) {
    throw new RuntimeException("not implemented");
  }
}

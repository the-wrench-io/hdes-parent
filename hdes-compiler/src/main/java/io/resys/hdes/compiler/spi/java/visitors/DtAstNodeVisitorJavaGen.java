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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DirectionType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.ExpressionValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Header;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HeaderRefValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicy;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.InOperation;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.LiteralValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AndOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.NotUnaryOperation;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtCodeSpec;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtCodeSpecPair;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtMethodSpec;

public class DtAstNodeVisitorJavaGen extends DtAstNodeVisitorTemplate<DtJavaSpec, TypeSpec> {
  
  private final static String HEADER_REF = "//header ref to be replaces";
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
      
      if(pair.getKey().isEmpty()) {
        statements.addStatement("result.add($L)", pair.getValue());  
      } else {
        statements.add("\r\n/** \r\n * $L \r\n */\r\n", row.getText())
        .beginControlFlow("if($L)", pair.getKey())
        .addStatement("result.add($L)", pair.getValue())
        .endControlFlow();
      }
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
      CodeBlock ruleCode = visitRule(rule).getValue();
      if(ruleCode.isEmpty()) {
        continue;
      }
      
      Header header = body.getHeaders().getValues().get(rule.getHeader());
      if (header.getDirection() == DirectionType.IN) {
        if (and) {
          key.add("\r\n  && ");
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

    Header header = body.getHeaders().getValues().get(node.getHeader());
    if (header.getDirection() == DirectionType.IN) {
      return visitInputRule(node, header);
    } else {
      return ImmutableDtCodeSpec.builder()
          .value(CodeBlock.builder()
              .add(".").add(header.getName())
              .add("($L)", visitLiteral(((LiteralValue) value).getValue()).getValue())
              .build())
          .build();
    }
  }
  
  private DtCodeSpec visitInputRule(Rule node, Header header) {
    RuleValue value = node.getValue();
    String getMethod = JavaNaming.getMethod(header.getName());
    
    if(value instanceof LiteralValue) {
      Literal literal = ((LiteralValue) value).getValue();
      CodeBlock literalCode = visitLiteral(literal).getValue();

      CodeBlock.Builder exp = CodeBlock.builder();
      if(literal.getType() == ScalarType.DECIMAL) {
        exp.add("input.$L().compareTo($L) == 0", getMethod, literalCode);
      } else if(literal.getType() == ScalarType.DATE) {
        exp.add("input.$L().compareTo($L) == 0", getMethod, literalCode);
      } else if(literal.getType() == ScalarType.DATE_TIME) {
        exp.add("input.$L().compareTo($L) == 0", getMethod, literalCode);
      } else if(literal.getType() == ScalarType.TIME) {
        exp.add("input.$L().compareTo($L) == 0", getMethod, literalCode);
      } else if(literal.getType() == ScalarType.STRING) {
        exp.add("input.$L().equals($L)", getMethod, literalCode);
      } else {
        exp.add("input.$L() == $L", getMethod, literalCode);
      }
      return ImmutableDtCodeSpec.builder().value(exp.build()).build();
      
    } else if(value instanceof ExpressionValue) {
      
      DtCodeSpec result = visitExpressionValue(((ExpressionValue) value));
      String inputName = CodeBlock.builder().add("input.$L()", getMethod).build().toString();
      
      return ImmutableDtCodeSpec.builder()
          .value(CodeBlock.builder().add(result.getValue().toString().replaceAll(HEADER_REF, inputName)).build())
          .build();
    }
    throw new IllegalArgumentException("Not implemented rule node: " + node);
  } 

  @Override
  public DtCodeSpec visitExpressionValue(ExpressionValue node) {
    DtCodeSpec child = visitExpressionRuleValue(node.getExpression());
    
    return ImmutableDtCodeSpec.builder().value(
        CodeBlock.builder()
            .add(child.getValue())
            .build())
        .build();
  }
  
  @Override
  public DtCodeSpec visitEqualityOperation(EqualityOperation node) {
    
    CodeBlock left = visitExpressionRuleValue(node.getLeft()).getValue();
    CodeBlock right = visitExpressionRuleValue(node.getRight()).getValue();
    
    String operation;
    switch (node.getType()) {
    case EQUAL: operation = "eq($L, $L)"; break;
    case NOTEQUAL: operation = "neq($L, $L)"; break;
    case GREATER: operation = "gt($L, $L)"; break;
    case GREATER_THEN: operation = "gte($L, $L)"; break;
    case LESS: operation = "lt($L, $L)"; break;
    case LESS_THEN: operation = "lte($L, $L)"; break;
    default: throw new HdesCompilerException(HdesCompilerException.builder().unknownDTExpressionOperation(node));
    }
    return ImmutableDtCodeSpec.builder().value(CodeBlock.builder().add(operation, left, right).build()).build();
  }
  
  @Override
  public DtCodeSpec visitInOperation(InOperation node) {
    StringBuilder values = new StringBuilder();
    
    for(Literal literal : node.getValues()) {
      if(values.length() > 0) {
        values.append(", ");
      }
      values.append(visitLiteral(literal).getValue().toString());
    }
    
    return ImmutableDtCodeSpec.builder().value(CodeBlock.builder()
        .add("$T.asList($L).contains($L)", Arrays.class, values.toString(), HEADER_REF).build())
        .build();
  }
  
  @Override
  public DtCodeSpec visitNotOperation(NotUnaryOperation node) {
    CodeBlock child = visitExpressionRuleValue(node.getValue()).getValue();    
    return ImmutableDtCodeSpec.builder().value(
        CodeBlock.builder().add("!").add(child).build())
        .build();
  }

  @Override
  public DtCodeSpec visitHeaderRefValue(HeaderRefValue node) {
    return ImmutableDtCodeSpec.builder().value(
        CodeBlock.builder()
        .add(HEADER_REF)
        .build())
    .build();
  }
  
  private DtCodeSpec visitExpressionRuleValue(AstNode node) {
    if(node instanceof Literal) {
      return visitLiteral((Literal) node);
      
    } else if(node instanceof HeaderRefValue) {
      return visitHeaderRefValue((HeaderRefValue) node);
      
    } else if(node instanceof InOperation) {
      return visitInOperation((InOperation) node);
      
    } else if(node instanceof NotUnaryOperation) {
      return visitNotOperation((NotUnaryOperation) node);
      
    } else if(node instanceof EqualityOperation) {
      return visitEqualityOperation((EqualityOperation) node);
      
    } else if(node instanceof AndOperation) {
      return visitAndOperation((AndOperation) node);
    }
    
    throw new HdesCompilerException(HdesCompilerException.builder().unknownDTExpressionNode(node));
  }
  
  @Override
    public DtCodeSpec visitAndOperation(AndOperation node) {
    CodeBlock left = visitExpressionRuleValue(node.getLeft()).getValue();
    CodeBlock right = visitExpressionRuleValue(node.getRight()).getValue();
    
    return ImmutableDtCodeSpec.builder().value(
        CodeBlock.builder()
            .add(left).add("\r\n  && ").add(right)
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

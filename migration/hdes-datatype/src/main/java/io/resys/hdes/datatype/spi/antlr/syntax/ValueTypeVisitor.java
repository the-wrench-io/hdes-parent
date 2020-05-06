package io.resys.hdes.datatype.spi.antlr.syntax;

/*-
 * #%L
 * hdes-datatype
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AmbiguousExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AndCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Args;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.BetweenExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Condition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ConditionalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DataTypeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateTimeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ErrorNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Literal;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.LiteralType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.OrCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Primary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Root;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Unary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnarySign;
import io.resys.hdes.datatype.api.ImmutableErrorNode;

public class ValueTypeVisitor implements DataTypeExpressionAstNode.Visitor<List<ErrorNode>, List<ErrorNode>> {

  private ValueTypeVisitor() {
    super();
  }

  public static ValueTypeVisitor from() {
    return new ValueTypeVisitor();
  }

  @Override
  public List<ErrorNode> visit(Primary element) {
    return visit(element.getValue());
  }

  @Override
  public List<ErrorNode> visit(AmbiguousExpression element) {
    return visit(element.getValue());
  }

  @Override
  public List<ErrorNode> visit(Literal element) {
    return Collections.emptyList();
  }

  @Override
  public List<ErrorNode> visit(DataTypeConversion element) {
    return visit(element.getValue());
  }

  @Override
  public List<ErrorNode> visit(DateConversion element) {
    List<ErrorNode> errors = new ArrayList<>(visit(element.getValue()));
    
    if (!(element.getValue() instanceof Literal)) {
      return errors;
    }
    Literal child = (Literal) element.getValue();
    try {
      LocalDate.parse(child.getValue(), TemplateVisitor.DATE_FORMATTER);
    } catch (Exception e) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Expecting date format as: ")
              .append(TemplateVisitor.DATE).append(" ")
              .append("but actual was: ")
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(DateTimeConversion element) {
    List<ErrorNode> errors = new ArrayList<>(visit(element.getValue()));
    
    if (!(element.getValue() instanceof Literal)) {
      return errors;
    }
    Literal child = (Literal) element.getValue();
    try {
      LocalDateTime.parse(child.getValue(), TemplateVisitor.DATE_TIME_FORMATTER);
    } catch (Exception e) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Expecting date time format as: ")
              .append(TemplateVisitor.DATE_TIME).append(" ")
              .append("but actual was: ")
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(Root element) {
    List<ErrorNode> errors = new ArrayList<>(visit(element.getValue()));
    DataTypeExpressionAstNode child = element.getValue();
    if (child.getLiteralType() != element.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Expression outcome must be of type: ")
              .append(element.getLiteralType()).append(" ")
              .append("but was: ").append(child.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(ConditionalExpression element) {
    Condition condition = element.getCondition();
    DataTypeExpressionAstNode left = element.getLeft();
    DataTypeExpressionAstNode right = element.getRight();
    List<ErrorNode> errors = new ArrayList<>();
    errors.addAll(visit(condition));
    errors.addAll(visit(left));
    errors.addAll(visit(right));
    if (left.getLiteralType() != right.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Conditional expression data type ON left side must match right side but was: ")
              .append(left.getLiteralType()).append(" = ").append(right.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }
  
  @Override
  public List<ErrorNode> visit(BetweenExpression element) {
    DataTypeExpressionAstNode value = element.getValue();
    DataTypeExpressionAstNode left = element.getLeft();
    DataTypeExpressionAstNode right = element.getRight();
    
    List<ErrorNode> errors = new ArrayList<>();
    errors.addAll(visit(value));
    errors.addAll(visit(left));
    errors.addAll(visit(right));

    if (left.getLiteralType() != right.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Between expression data type ON left side must match value side but was: ")
              .append(value.getLiteralType()).append(" = ").append(right.getLiteralType())
              .toString())
          .target(element)
          .build());
    } else if (left.getLiteralType() != right.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Conditional expression data type ON right side must match value side but was: ")
              .append(value.getLiteralType()).append(" = ").append(left.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(MethodInvocation element) {
    DataType dataType = element.getDependency();
    List<DataType> output = dataType == null ? Collections.emptyList() : dataType.getProperties().stream().filter(d -> d.getDirection() == Direction.OUT).collect(Collectors.toList());
    List<ErrorNode> errors = new ArrayList<>(visit(element.getArgs()));
    if (dataType == null) {
      errors.add(ImmutableErrorNode.builder()
          .message("Undefined method")
          .target(element)
          .build());
      return errors;
    }
    if (output.isEmpty()) {
      errors.add(ImmutableErrorNode.builder()
          .message("Method invocation return type is not defined")
          .target(element)
          .build());
      return errors;
    }
    if (output.size() > 1) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder("Method invocation can have only one return type but was: ").append(output.size()).toString())
          .target(element)
          .build());
      return errors;
    }
    // validate inputs
    List<DataType> inputs = dataType.getProperties().stream().filter(d -> d.getDirection() == Direction.IN).collect(Collectors.toList());
    for (DataType input : inputs) {
      if (TemplateVisitor.convert(input.getValueType()) == LiteralType.UNKNOWN) {
        errors.add(ImmutableErrorNode.builder()
            .message(new StringBuilder("Method invocation input type: '")
                .append(input.getName())
                .append("' could not be determined").toString())
            .target(element)
            .build());
      }
    }
    LiteralType literalType = TemplateVisitor.convert(output.get(0).getValueType());
    if (literalType == LiteralType.UNKNOWN) {
      errors.add(ImmutableErrorNode.builder()
          .message("Method invocation return type could not be determined")
          .target(element)
          .build());
      return errors;
    }
    // only one type and type is array
    List<DataTypeExpressionAstNode> args = element.getArgs() == null ? Collections.emptyList() : element.getArgs().getValues();
    if (args.isEmpty() && inputs.size() <= 1 && inputs.get(0).isArray()) {
      return errors;
    }
    if (inputs.size() == 1 && inputs.get(0).isArray()) {
      DataType input = inputs.get(0);
      List<LiteralType> types = input.getValueType() != ValueType.NUMERIC ? Arrays.asList(TemplateVisitor.convert(input.getValueType())) : Arrays.asList(LiteralType.DECIMAL, LiteralType.INTEGER);
      int index = 0;
      for (DataTypeExpressionAstNode child : args) {
        index++;
        if (types.contains(child.getLiteralType())) {
          continue;
        }
        errors.add(ImmutableErrorNode.builder()
            .message(new StringBuilder("Method invocation argument at: ").append(index).append(" ")
                .append("expecting type: ").append(types)
                .append("but was: ").append(child.getLiteralType())
                .toString())
            .target(element)
            .build());
      }
      return errors;
    } else if (args.size() != inputs.size()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder("Method invocation expecting number of args: ").append(inputs.size()).append(" ")
              .append("but was: ").append(args.size())
              .toString())
          .target(element)
          .build());
      return errors;
    }
    int index = 0;
    for (DataTypeExpressionAstNode child : args) {
      LiteralType input = TemplateVisitor.convert(inputs.get(index++).getValueType());
      if (child.getLiteralType() != input) {
        errors.add(ImmutableErrorNode.builder()
            .message(new StringBuilder("Method invocation argument at: ").append(index).append(" ")
                .append("expecting type: ").append(input)
                .append("but was: ").append(child.getLiteralType())
                .toString())
            .target(element)
            .build());
      }
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(Condition element) {
    DataTypeExpressionAstNode left = element.getLeft();
    DataTypeExpressionAstNode right = element.getRight();
    List<ErrorNode> errors = new ArrayList<>();
    errors.addAll(visit(left));
    errors.addAll(visit(right));
    if (left.getLiteralType() != right.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Conditional expression data type ON left side must match right side but was: ")
              .append(left.getLiteralType()).append(" = ").append(right.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(TypeName element) {
    List<ErrorNode> errors = new ArrayList<>();
    LiteralType literal = element.getLiteralType();
    if (element.getDependency() == null) {
      errors.add(ImmutableErrorNode.builder()
          .message("Undefined type name")
          .target(element)
          .build());
    } else if (element.getDependency() != null && literal == LiteralType.UNKNOWN) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder("Type: ").append(element.getValue())
              .append(" can't be used because it refers to external type: ")
              .append(element.getDependency().getValueType())
              .append(", that is converted into: ").append(literal)
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(Unary element) {
    DataTypeExpressionAstNode child = element.getValue();
    List<ErrorNode> errors = new ArrayList<>(visit(child));
    if (element.getSign() == UnarySign.NOT && child.getLiteralType() != LiteralType.BOOLEAN) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder("Expecting boolean type in unary NOT(!) operation but was: ").append(child.getLiteralType())
              .toString())
          .target(element)
          .build());
    } else if (child.getLiteralType() != LiteralType.DECIMAL && child.getLiteralType() != LiteralType.INTEGER) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder("Expecting numerical type(").append(LiteralType.DECIMAL).append(", ").append(LiteralType.INTEGER).append(") ")
              .append("but was: ").append(child.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(AndCondition element) {
    DataTypeExpressionAstNode left = element.getLeft();
    DataTypeExpressionAstNode right = element.getRight();
    List<ErrorNode> errors = new ArrayList<>();
    errors.addAll(visit(left));
    errors.addAll(visit(right));
    if (left.getLiteralType() != right.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Conditional expression OR(|) data type ON left side must match right side but was: ")
              .append(left.getLiteralType()).append(" = ").append(right.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(OrCondition element) {
    DataTypeExpressionAstNode left = element.getLeft();
    DataTypeExpressionAstNode right = element.getRight();
    List<ErrorNode> errors = new ArrayList<>();
    errors.addAll(visit(left));
    errors.addAll(visit(right));
    if (left.getLiteralType() != right.getLiteralType()) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Conditional expression AND(&) data type ON left side must match right side but was: ")
              .append(left.getLiteralType()).append(" = ").append(right.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(ArithmeticalExpression element) {
    DataTypeExpressionAstNode left = element.getLeft();
    DataTypeExpressionAstNode right = element.getRight();
    List<ErrorNode> errors = new ArrayList<>();
    errors.addAll(visit(left));
    errors.addAll(visit(right));
    LiteralType leftType = TemplateVisitor.toNumeric(left.getLiteralType());
    LiteralType rightType = TemplateVisitor.toNumeric(right.getLiteralType());
    if (element.getType() == ArithmeticalType.ADD) {
      // Adding is supported for mixed types
    } else if (leftType != LiteralType.NUMERIC || rightType != LiteralType.NUMERIC) {
      errors.add(ImmutableErrorNode.builder()
          .message(new StringBuilder()
              .append("Arithmetical expression (" + element.getType() + ") data types must be: " + LiteralType.NUMERIC).append(" ")
              .append("but was: ")
              .append(left.getLiteralType()).append(" = ").append(right.getLiteralType())
              .toString())
          .target(element)
          .build());
    }
    return errors;
  }

  @Override
  public List<ErrorNode> visit(Args element) {
    if (element == null) {
      return Collections.emptyList();
    }
    List<ErrorNode> errors = new ArrayList<>();
    element.getValues().stream().forEach(a -> errors.addAll(visit(a)));
    return errors;
  }

  @Override
  public List<ErrorNode> visit(MethodName element) {
    throw new RuntimeException("AstNode visiting error, methodName can't be visited directly, every arg should be validated on parent level!");
  }

  private List<ErrorNode> visit(DataTypeExpressionAstNode element) {
    return TemplateVisitor.visit(this, element);
  }
}

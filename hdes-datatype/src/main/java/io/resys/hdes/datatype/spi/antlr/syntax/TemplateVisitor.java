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

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AmbiguousExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AndCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Args;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.BetweenExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Condition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ConditionalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DataTypeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateTimeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Literal;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.LiteralType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.OrCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Primary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Unary;
import io.resys.hdes.datatype.spi.antlr.errors.AstNodeException;

public class TemplateVisitor {

  public static final String DATE = "yyyy-MM-dd";
  public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE);
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME);

  public static <T, R, V extends DataTypeExpressionAstNode.Visitor<T, R>> T visit(V visitor, DataTypeExpressionAstNode element) {
    log(element);
    if (element instanceof AmbiguousExpression) {
      return visitor.visit((AmbiguousExpression) element);
    } else if (element instanceof ConditionalExpression) {
      return visitor.visit((ConditionalExpression) element);
    } else if (element instanceof DataTypeConversion) {
      return visitor.visit((DataTypeConversion) element);
    } else if (element instanceof ArithmeticalExpression) {
      return visitor.visit((ArithmeticalExpression) element);
    } else if (element instanceof OrCondition) {
      return visitor.visit((OrCondition) element);
    } else if (element instanceof AndCondition) {
      return visitor.visit((AndCondition) element);
    } else if (element instanceof Condition) {
      return visitor.visit((Condition) element);
    } else if (element instanceof MethodName) {
      return visitor.visit((MethodName) element);
    } else if (element instanceof MethodInvocation) {
      return visitor.visit((MethodInvocation) element);
    } else if (element instanceof Args) {
      return visitor.visit((Args) element);
    } else if (element instanceof TypeName) {
      return visitor.visit((TypeName) element);
    } else if (element instanceof Literal) {
      return visitor.visit((Literal) element);
    } else if (element instanceof Unary) {
      return visitor.visit((Unary) element);
    } else if (element instanceof Primary) {
      return visitor.visit((Primary) element);
    } else if (element instanceof BetweenExpression) {
      return visitor.visit((BetweenExpression) element);
    } else if (element instanceof DateConversion) {
      return visitor.visit((DateConversion) element);
    } else if (element instanceof DateTimeConversion) {
      return visitor.visit((DateTimeConversion) element);
    }
    throw new AstNodeException("Unknown node: " + element.getClass() + "!");
  }
  
  public static void log(DataTypeExpressionAstNode element) {
    // System.out.println("visiting: " + element.getClass());
  }

  public static LiteralType toNumeric(LiteralType valueType) {
    switch (valueType) {
    case INTEGER:
    case DECIMAL:
    case NUMERIC:
      return LiteralType.NUMERIC;
    default:
      return valueType;
    }
  }

  public static LiteralType convert(ValueType valueType) {
    switch (valueType) {
    case BOOLEAN:
      return LiteralType.BOOLEAN;
    case INTEGER:
    case LONG:
      return LiteralType.INTEGER;
    case DECIMAL:
      return LiteralType.DECIMAL;
    case NUMERIC:
      return LiteralType.NUMERIC;
    case STRING:
      return LiteralType.STRING;
    case DATE:
      return LiteralType.DATE;
    case DATE_TIME:
      return LiteralType.DATE_TIME;
    default:
      return LiteralType.UNKNOWN;
    }
  }

  public static LiteralType combine(LiteralType arg0, LiteralType arg1) {
    Set<LiteralType> types = new HashSet<>();
    types.add(arg0);
    types.add(arg1);
    if (types.size() == 1) {
      return arg0;
    }
    if (types.contains(LiteralType.STRING) && types.contains(LiteralType.DATE_TIME)) {
      return LiteralType.DATE_TIME;
    } else if (types.contains(LiteralType.STRING) && types.contains(LiteralType.DATE)) {
      return LiteralType.DATE;
    } else if (types.contains(LiteralType.DECIMAL) && types.contains(LiteralType.INTEGER)) {
      return LiteralType.DECIMAL;
    }
    return LiteralType.STRING;
  }
}

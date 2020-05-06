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

import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AmbiguousExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AndCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Args;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.BetweenExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.CompilationUnit;
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
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Root;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Unary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnarySign;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnaryType;
import io.resys.hdes.datatype.api.ImmutableCompilationUnit;
import io.resys.hdes.datatype.api.ImmutableLiteral;
import io.resys.hdes.datatype.spi.antlr.errors.AstNodeException;

public class JavaSyntaxVisitor implements DataTypeExpressionAstNode.Visitor<String, CompilationUnit> {
  private JavaSyntaxVisitor() {
    super();
  }

  public static CompilationUnit from(DataTypeExpressionAstNode node) {
    return new JavaSyntaxVisitor().visit((Root) node);
  }

  @Override
  public CompilationUnit visit(Root element) {
    String expression = visit(element.getValue());
    return ImmutableCompilationUnit.builder()
        .value(expression)
        .build();
  }
  @Override
  public String visit(Primary element) {
    return new StringBuilder()
        .append("(")
            .append(visit(element.getValue()))
            .append(")").toString();
  }

  @Override
  public String visit(AmbiguousExpression element) {
    return visit(element.getValue());
  }

  @Override
  public String visit(Literal element) {
    StringBuilder result = new StringBuilder();
    if (element.getLiteralType() == LiteralType.STRING) {
      result.append("\"").append(element.getValue()).append("\"");
    } else if (element.getLiteralType() == LiteralType.DECIMAL) {
      result.append("new BigDecimal(\"").append(element.getValue()).append("\")");
    } else {
      result.append(element.getValue());
    }
    return result.toString();
  }

  @Override
  public String visit(ConditionalExpression element) {
    return new StringBuilder()
        .append(visit(element.getCondition())).append(" ? ")
        .append(visit(element.getLeft())).append(" : ")
        .append(visit(element.getRight()))
        .toString();
  }

  @Override
  public String visit(MethodInvocation element) {
    StringBuilder result = new StringBuilder();
    if (element.getTypeName() != null) {
      result.append(visit(element.getTypeName()));
    }
    return result
        .append(visit(element.getName()))
        .append(visit(element.getArgs()))
        .toString();
  }

  @Override
  public String visit(Args element) {
    if(element == null) {
      return "()";
    }

    StringBuilder result = new StringBuilder();
    for(DataTypeExpressionAstNode expression : element.getValues()) {
      if(result.length() > 0) {
        result.append(", ");
      }
      result.append(visit(expression));
    }
    return result.insert(0, "(").append(")").toString();
  }

  @Override
  public String visit(MethodName element) {
    return element.getValue();
  }

  @Override
  public String visit(Condition element) {
    return new StringBuilder()
        .append(visit(element.getLeft())).append(" ")
        .append(element.getSign().getValue().equals("=") ? "==" : element.getSign()).append(" ")
        .append(visit(element.getRight()))
        .toString();
  }

  @Override
  public String visit(TypeName element) {
    return element.getValue();
  }

  @Override
  public String visit(Unary element) {

    String sign = "";
    if(element.getSign() == UnarySign.ADD) {
      sign = "+";
    } else if(element.getSign() == UnarySign.DEC) {
      sign = "--";
    } else if(element.getSign() == UnarySign.INC) {
      sign = "++";
    } else if(element.getSign() == UnarySign.NOT) {
      sign = "!";
    } else if(element.getSign() == UnarySign.SUB) {
      sign = "-";
    }

    if(element.getValue() instanceof Literal) {
      Literal src = (Literal) element.getValue();
      String newValue = element.getType() == UnaryType.POSTFIX ? 
          src.getValue() + sign : sign + src.getValue();
      Literal target = ImmutableLiteral.builder()
          .from(src)
          .value(newValue)
          .build();
      return visit(target);
    }

    String value = visit(element.getValue());
    return element.getType() == UnaryType.POSTFIX ? value + sign : sign + value;
  }

  @Override
  public String visit(AndCondition element) {
    return new StringBuilder()
        .append(visit(element.getLeft()))
        .append(" && ")
        .append(visit(element.getRight()))
        .toString();
  }
  @Override
  public String visit(OrCondition element) {
    return new StringBuilder()
        .append(visit(element.getLeft()))
        .append(" || ")
        .append(visit(element.getRight()))
        .toString();
  }
  @Override
  public String visit(ArithmeticalExpression element) {
    String sign = null;
    if(element.getType() == ArithmeticalType.ADD) {
      sign = "+";
    } else if(element.getType() == ArithmeticalType.SUBSTRACT) {
      sign = "-";
    } else if(element.getType() == ArithmeticalType.MULTIPLY) {
      sign = "*";
    } else {
      sign = "/";
    }

    return new StringBuilder()
        .append(visit(element.getLeft()))
        .append(" ").append(sign).append(" ")
        .append(visit(element.getRight()))
        .toString();
  }
  
  @Override
  public String visit(DataTypeConversion element) {
    return visit(element.getValue());
  }

  private String visit(DataTypeExpressionAstNode element) {
    if (element instanceof AmbiguousExpression) {
      return visit((AmbiguousExpression) element);
    } else if (element instanceof ConditionalExpression) {
      return visit((ConditionalExpression) element);

    } else if (element instanceof DataTypeConversion) {
      return visit((DataTypeConversion) element);

      
    } else if (element instanceof ArithmeticalExpression) {
      return visit((ArithmeticalExpression) element);
    } else if (element instanceof OrCondition) {
      return visit((OrCondition) element);
    } else if (element instanceof AndCondition) {
      return visit((AndCondition) element);

    } else if (element instanceof Condition) {
      return visit((Condition) element);
    } else if (element instanceof MethodName) {
      return visit((MethodName) element);

    } else if (element instanceof MethodInvocation) {
      return visit((MethodInvocation) element);
    } else if (element instanceof Args) {
      return visit((Args) element);

    } else if (element instanceof TypeName) {
      return visit((TypeName) element);
    } else if (element instanceof Literal) {
      return visit((Literal) element);
    } else if (element instanceof Unary) {
      return visit((Unary) element);
    } else if (element instanceof Primary) {
      return visit((Primary) element);
      
    } else if (element instanceof DateConversion) {
      return visit((DateConversion) element);
    } else if (element instanceof DateTimeConversion) {
      return visit((DateTimeConversion) element);
    } else if (element instanceof BetweenExpression) {
      return visit((BetweenExpression) element);
    }
    throw new AstNodeException("Unknown node: " + element.getClass() + "!");
  }

  @Override
  public String visit(DateConversion element) {
    return visit(element.getValue());
  }

  @Override
  public String visit(DateTimeConversion element) {
    return visit(element.getValue());
  }

  @Override
  public String visit(BetweenExpression element) {
    return "";
  }
}

package io.resys.hdes.datatype.spi.antlr;

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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import io.resys.hdes.datatype.DataTypeLexer;
import io.resys.hdes.datatype.DataTypeParser;
import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataTypeExpression;
import io.resys.hdes.datatype.api.DataTypeExpression.Target;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.CompilationUnit;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DependencyTree;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ErrorNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocationDependency;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeNameDependency;
import io.resys.hdes.datatype.api.DataTypeService.ModelBuilder;
import io.resys.hdes.datatype.api.ImmutableDataTypeExpression;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.datatype.spi.antlr.dependencies.MethodInvocationDelegateDependency;
import io.resys.hdes.datatype.spi.antlr.dependencies.TypeNameDelegateDependency;
import io.resys.hdes.datatype.spi.antlr.errors.AntlrErrorListener;
import io.resys.hdes.datatype.spi.antlr.errors.AstNodeException;
import io.resys.hdes.datatype.spi.antlr.syntax.AstNodeVisitor;
import io.resys.hdes.datatype.spi.antlr.syntax.DependencyTreeVisitor;
import io.resys.hdes.datatype.spi.antlr.syntax.JavaSyntaxVisitor;
import io.resys.hdes.datatype.spi.antlr.syntax.ValueTypeVisitor;
import io.resys.hdes.datatype.spi.builders.DataTypeBuilder;

public class DataTypeExpressionFactory {
  public static class Builder {
    private Target targetType;
    private List<MethodInvocationDependency> methodInvocationDependency = new ArrayList<>();
    private List<TypeNameDependency> typeNameDependency = new ArrayList<>();
    private DataType returnType;
    private String value;
    private boolean strict;

    public Builder target(Target targetType) {
      this.targetType = targetType;
      return this;
    }

    public Builder value(String value) {
      this.value = value;
      return this;
    }

    public Builder strict() {
      this.strict = true;
      return this;
    }

    public Builder returnType(Function<ModelBuilder, DataType> type) {
      returnType = type.apply(new DataTypeBuilder().direction(Direction.OUT));
      return this;
    }

    public Builder dependency(MethodInvocationDependency type) {
      methodInvocationDependency.add(type);
      return this;
    }

    public Builder dependency(TypeNameDependency type) {
      typeNameDependency.add(type);
      return this;
    }

    public DataTypeExpression build() {
      Assert.notNull(returnType, () -> "returnType must be defined");
      Assert.notNull(targetType, () -> "targetType must be defined");
      Assert.notNull(value, () -> "value must be defined");
      
      AntlrErrorListener antlrErrorListener = new AntlrErrorListener();
      
      DataTypeLexer lexer = new DataTypeLexer(CharStreams.fromString(value));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      
      DataTypeParser parser = new DataTypeParser(tokens);
      //parser.removeErrorListeners();
      parser.addErrorListener(antlrErrorListener);
      
      ParseTree tree = parser.compilationUnit();
      
      // Expression language ast tree
      DependencyTree dependencyTree = DependencyTreeVisitor.from(
          returnType, 
          MethodInvocationDelegateDependency.from(this.methodInvocationDependency), 
          TypeNameDelegateDependency.from(this.typeNameDependency))
      .visit((DataTypeExpressionAstNode.Root) tree.accept(new AstNodeVisitor()));
      
      List<ErrorNode> errors = ValueTypeVisitor.from().visit(dependencyTree.getNode());
      errors.addAll(antlrErrorListener.getErrors());
          
      DataTypeExpression expression = ImmutableDataTypeExpression.builder()
          .astTree(dependencyTree)
          .addAllErrors(errors)
          .returnType(returnType)
          .build();
      
      if (!expression.getErrors().isEmpty() && strict) {
        throw new AstNodeException(expression.getErrors());
      }
      
      // Compilation lang
      CompilationUnit result = JavaSyntaxVisitor.from(dependencyTree.getNode());
      
      
      return expression;
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}

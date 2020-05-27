package io.resys.hdes.compiler.spi.java.visitors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

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

import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Code;
import io.resys.hdes.compiler.api.HdesCompiler.CodeValue;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.api.ImmutableCode;
import io.resys.hdes.compiler.api.ImmutableCodeValue;
import io.resys.hdes.compiler.spi.NamingContext;
import io.resys.hdes.compiler.spi.java.JavaNamingContext;

public class JavaAstEnvirVisitor {
  private final NamingContext naming = JavaNamingContext.config().build();
  
  public Code visit(AstEnvir envir) {
    List<CodeValue> values = new ArrayList<>();
    for(AstNode ast : envir.getBody().values()) {
      
      if(ast instanceof DecisionTableBody) {
        values.addAll(visit((DecisionTableBody) ast));
      } else if(ast instanceof FlowBody) {
        values.addAll(visit((FlowBody) ast, envir));
      } else {
        throw new HdesCompilerException(HdesCompilerException.builder().unknownAst(ast));
      }
      
    }
    
    return ImmutableCode.builder().values(values).build();
  }
  
  private List<CodeValue> visit(DecisionTableBody body) {
    StringBuilder interfaceBuilder = new StringBuilder();
    StringBuilder implementationBuilder = new StringBuilder();
    
    TypeSpec superInterface = visitDt(body, new DtAstNodeVisitorJavaInterface(naming).visitDecisionTableBody(body), interfaceBuilder);
    TypeSpec implementation = visitDt(body, new DtAstNodeVisitorJavaGen(naming).visitDecisionTableBody(body), implementationBuilder);
    return Arrays.asList(
        ImmutableCodeValue.builder().type(HdesCompiler.SourceType.DT)
          .source(body.getToken().getText())
          .target(interfaceBuilder.toString())
          .simpleName(superInterface.name)
          .packageName(naming.dt().pkg(body))
          .build(),
        ImmutableCodeValue.builder().type(HdesCompiler.SourceType.DT)
          .source(body.getToken().getText())
          .target(implementationBuilder.toString())
          .simpleName(implementation.name)
          .packageName(naming.dt().pkg(body))
          .build()
        );
  }
  private List<CodeValue> visit(FlowBody body, AstEnvir envir) {
    StringBuilder interfaceBuilder = new StringBuilder();
    StringBuilder implementationBuilder = new StringBuilder();
    TypeSpec superInterface = visitFlow(body, new FlAstNodeVisitorJavaInterface(naming).visitBody(body), interfaceBuilder);
    TypeSpec implementation = visitFlow(body, new FlAstNodeVisitorJavaGen(naming).visitBody(body), implementationBuilder);
    
    return Arrays.asList(
        ImmutableCodeValue.builder().type(HdesCompiler.SourceType.FL)
          .source(body.getToken().getText())
          .target(interfaceBuilder.toString())
          .simpleName(superInterface.name)
          .packageName(naming.fl().pkg(body))
          .build(),
        ImmutableCodeValue.builder().type(HdesCompiler.SourceType.FL)
          .source(body.getToken().getText())
          .target(implementationBuilder.toString())
          .simpleName(implementation.name)
          .packageName(naming.fl().pkg(body))
          .build()
        );
  }

  private TypeSpec visitFlow(FlowBody body, TypeSpec type, Appendable appendable) {
    try {
      JavaFile file = JavaFile.builder(naming.fl().pkg(body), type).build();
      file.writeTo(appendable);
      return type;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  private TypeSpec visitDt(DecisionTableBody body, TypeSpec type, Appendable appendable) {
    try {
      JavaFile file = JavaFile.builder(naming.dt().pkg(body), type).build();
      file.writeTo(appendable);
      return type;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}

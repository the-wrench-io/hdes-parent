package io.resys.hdes.compiler.spi.java.visitors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.squareup.javapoet.ClassName;
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
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.api.HdesCompiler.TypeName;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.api.HdesExecutable;
import io.resys.hdes.compiler.api.ImmutableResource;
import io.resys.hdes.compiler.api.ImmutableTypeDeclaration;
import io.resys.hdes.compiler.api.ImmutableTypeName;
import io.resys.hdes.compiler.spi.NamingContext;
import io.resys.hdes.compiler.spi.java.JavaNamingContext;

public class JavaAstEnvirVisitor {
  private final NamingContext naming = JavaNamingContext.config().build();
  
  public List<Resource> visit(AstEnvir envir) {
    List<Resource> values = new ArrayList<>();
    for(AstNode ast : envir.getBody().values()) {
      
      if(ast instanceof DecisionTableBody) {
        values.add(visit((DecisionTableBody) ast));
      } else if(ast instanceof FlowBody) {
        values.add(visit((FlowBody) ast, envir));
      } else {
        throw new HdesCompilerException(HdesCompilerException.builder().unknownAst(ast));
      }
      
    }
    
    return Collections.unmodifiableList(values);
  }
  
  private Resource visit(DecisionTableBody body) {
    final var pkg = naming.dt().pkg(body);
    final var interfaceBuilder = new StringBuilder();
    final var implementationBuilder = new StringBuilder();
    
    final TypeSpec superInterface = visitDt(body, new DtAstNodeVisitorJavaInterface(naming).visitDecisionTableBody(body), interfaceBuilder);
    final TypeSpec implementation = visitDt(body, new DtAstNodeVisitorJavaGen(naming).visitDecisionTableBody(body), implementationBuilder);
    final var interfaceType = ImmutableTypeName.builder().name(superInterface.name).pkg(pkg).build();
    final var implementationType = ImmutableTypeName.builder().name(implementation.name).pkg(pkg).build();
    
    final var nestedPkg = pkg + "." + superInterface.name;
    final var types = superInterface.typeSpecs.stream()
        .map(spec -> ImmutableTypeName.builder().name(spec.name).pkg(nestedPkg).build()).collect(Collectors.toList());
    types.add(interfaceType);
    types.add(implementationType);
    
    return ImmutableResource.builder()
        .type(HdesExecutable.SourceType.DT).name(body.getId()).types(types).source(body.getToken().getText())
        
        .input(visitNestedTypeName(naming.dt().input(body)))
        .output(visitNestedTypeName(naming.dt().output(body)))
        
        .addDeclarations(ImmutableTypeDeclaration.builder().type(interfaceType).value(interfaceBuilder.toString()).isExecutable(false).build())
        .addDeclarations(ImmutableTypeDeclaration.builder().type(implementationType).value(implementationBuilder.toString()).isExecutable(true).build())
        .build();
  }
  
  private Resource visit(FlowBody body, AstEnvir envir) {
    StringBuilder interfaceBuilder = new StringBuilder();
    StringBuilder implementationBuilder = new StringBuilder();
    TypeSpec superInterface = visitFlow(body, new FlAstNodeVisitorJavaInterface(naming).visitBody(body), interfaceBuilder);
    TypeSpec implementation = visitFlow(body, new FlAstNodeVisitorJavaGen(naming).visitBody(body), implementationBuilder);

    return ImmutableResource.builder()
        .type(HdesExecutable.SourceType.FL)
        .name(body.getId())
        .source(body.getToken().getText())
        
        .input(visitNestedTypeName(naming.fl().input(body)))
        .output(visitNestedTypeName(naming.fl().output(body)))
        
        .addDeclarations(ImmutableTypeDeclaration.builder()
            .type(ImmutableTypeName.builder().name(superInterface.name).pkg(naming.fl().pkg(body)).build())
            .isExecutable(false).value(interfaceBuilder.toString()).build())
        
        .addDeclarations(ImmutableTypeDeclaration.builder()
            .type(ImmutableTypeName.builder().name(implementation.name).pkg(naming.fl().pkg(body)).build())
            .isExecutable(true).value(implementationBuilder.toString()).build())
        
        .build();
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
  
  private static TypeName visitNestedTypeName(ClassName name) {
   return ImmutableTypeName.builder().name(name.simpleName()).pkg(name.packageName()).build(); 
  }
}

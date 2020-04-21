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

import java.util.function.Function;

import javax.lang.model.element.Modifier;

import org.immutables.value.Value.Immutable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.AstNode.DirectionType;
import io.resys.hdes.ast.api.nodes.AstNode.Headers;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TypeDefNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.compiler.spi.NamingContext;
import io.resys.hdes.compiler.spi.java.JavaSpecUtil;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtMethodSpec;
import io.resys.hdes.compiler.spi.java.visitors.DtJavaSpec.DtTypesSpec;

public class DtAstNodeVisitorJavaInterface extends DtAstNodeVisitorTemplate<DtJavaSpec, TypeSpec> {

  private final NamingContext naming;
  private DecisionTableBody body;

  public DtAstNodeVisitorJavaInterface(NamingContext naming) {
    super();
    this.naming = naming;
  }
  
  @Override
  public TypeSpec visitDecisionTableBody(DecisionTableBody node) {
    this.body = node;
    return TypeSpec.interfaceBuilder(naming.dt().interfaze(node))
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(naming.dt().superinterface(node))
        .addTypes(visitHeaders(node.getHeaders()).getValues()).build();
  }
  
  @Override
  public DtTypesSpec visitHeaders(Headers node) {
    Function<ClassName, TypeSpec.Builder> from = (name) -> TypeSpec
        .interfaceBuilder(name)
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    
    TypeSpec.Builder inputBuilder = from.apply(naming.dt().input(body))
        .addSuperinterface(naming.dt().inputSuperinterface(body));

    TypeSpec.Builder outputBuilder = from.apply(naming.dt().output(body))
        .addSuperinterface(naming.dt().outputSuperinterface(body));
    
    for(TypeDefNode header : node.getValues()) {
      MethodSpec method = visitHeader(header).getValue();
      if(header.getDirection() == DirectionType.IN) {
        inputBuilder.addMethod(method);
      } else {
        outputBuilder.addMethod(method);        
      }
    }
    return ImmutableDtTypesSpec.builder()
        .addValues(inputBuilder.build(), outputBuilder.build())
        .build();
  }

  @Override
  public DtMethodSpec visitHeader(TypeDefNode node) {
    ScalarTypeDefNode scalar = (ScalarTypeDefNode) node;
    MethodSpec method = MethodSpec.methodBuilder(JavaSpecUtil.getMethod(node.getName()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(JavaSpecUtil.type(scalar.getType()))
        .build();
    return ImmutableDtMethodSpec.builder().value(method).build();
  }
}

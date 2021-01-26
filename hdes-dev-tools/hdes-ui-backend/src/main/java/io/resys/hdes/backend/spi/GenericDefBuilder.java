package io.resys.hdes.backend.spi;

/*-
 * #%L
 * hdes-ui-backend
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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.resys.hdes.ast.api.nodes.BodyNode;
import io.resys.hdes.ast.api.nodes.BodyNode.EmptyBody;
import io.resys.hdes.ast.api.nodes.BodyNode.TypeDef;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.HdesNode.ErrorNode;
import io.resys.hdes.ast.api.nodes.RootNode;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefAst;
import io.resys.hdes.backend.api.HdesBackend.DefError;
import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.api.ImmutableDef;
import io.resys.hdes.backend.api.ImmutableDefAst;
import io.resys.hdes.backend.api.ImmutableDefError;
import io.resys.hdes.backend.api.ImmutableDefErrorToken;

public class GenericDefBuilder {
  
  public void from(RootNode astEnvir, Consumer<Def> consumer) {
    for (String id : astEnvir.getBody().keySet()) {
      
      BodyNode node = astEnvir.getBody(id);
      String src = node.getToken().getText();
      List<ErrorNode> errors = astEnvir.getErrors(id);
      DefType type = null;
      DefAst ast = null;
      
      if (node instanceof DecisionTableBody) {
        type = DefType.DT;
        ast = visit((DecisionTableBody) node, astEnvir);
      } else if (node instanceof FlowBody) {
        type = DefType.FL;
        ast = visit((FlowBody) node, astEnvir);
      } else {
        continue;
      }
     
      Def def = ImmutableDef.builder()
          .id(id)
          .value(src)
          .type(type)
          .name(node.getId().getValue())
          .errors(errors.stream().map(e -> map(id, node, e)).collect(Collectors.toUnmodifiableList()))
          .ast(ast).build();
      consumer.accept(def);
    }
  }
  
  private DefAst visit(DecisionTableBody body, RootNode envir) {
    List<TypeDef> inputs = body.getHeaders().getAcceptDefs();
    
    return ImmutableDefAst.builder().inputs(inputs).build();
  }
  
  private DefAst visit(FlowBody body, RootNode envir) {
  
    return ImmutableDefAst.builder().build();
  }
  
  private DefAst visit(EmptyBody body, RootNode envir) {
    
    return ImmutableDefAst.builder().build();
  }
  
  private DefError map(String key, BodyNode body, ErrorNode node) {
    return ImmutableDefError.builder()
        .id(key)
        .name(body.getId().getValue())
        .message(node.getMessage())
        .token(ImmutableDefErrorToken.builder()
            .line(node.getTarget().getToken().getStart().getLine())
            .column(node.getTarget().getToken().getStart().getCol())
            .text(node.getTarget().getToken().getText())
            .msg(node.getMessage())
            .build())
        .build();
  }
  
  public static GenericDefBuilder create() {
    return new GenericDefBuilder();
  }
}

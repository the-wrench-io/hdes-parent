package io.resys.hdes.ast.api;

import java.util.List;

/*-
 * #%L
 * hdes-ast
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

import java.util.Map;

import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.ErrorNode;

public interface AstEnvir {
  Map<String, BodyNode> getBody();
  BodyNode getBody(String id);
  String getSrc(String id);
  Map<String, List<ErrorNode>> getErrors();
  
  interface Builder {
    Builder ignoreErrors();
    Builder from(AstEnvir envir);
    SourceBuilder<Builder> add();
    AstEnvir build();
  }
  
  interface SourceBuilder<R> {
    SourceBuilder<R> externalId(String externalId);
    R src(String src);
  }
}

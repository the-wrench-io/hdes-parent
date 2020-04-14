package io.resys.hdes.ast.spi;

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

import java.util.List;

import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.nodes.AstNode;

public class ImmutableAstEnvir implements AstEnvir {
  
  private final List<AstNode> nodes;
  private final AstSources sources;

  public ImmutableAstEnvir(List<AstNode> nodes, AstSources sources) {
    super();
    this.nodes = nodes;
    this.sources = sources;
  }
  @Override
  public AstSources getSource() {
    return sources;
  }
  @Override
  public List<AstNode> getNodes() {
    return nodes;
  } 
  
  
  public static Builder builder() {
    return new GenericBuilder();
  }
  
  public static class GenericBuilder implements Builder {

    private boolean strict;
    
    @Override
    public Builder from(AstEnvir envir) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public Builder strict() {
      strict = true;
      return this;
    }

    @Override
    public AstEnvir build() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public SourceBuilder<Builder> add() {
      // TODO Auto-generated method stub
      return null;
    } 
  }
}

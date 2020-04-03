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

import io.resys.hdes.ast.api.HdesEnvir;
import io.resys.hdes.ast.api.nodes.AstNode;

public class ImmutableHdesEnvir implements HdesEnvir {
  
  private final List<AstNode> nodes;
  private final Source sources;

  public ImmutableHdesEnvir(List<AstNode> nodes, Source sources) {
    super();
    this.nodes = nodes;
    this.sources = sources;
  }
  @Override
  public Source getSource() {
    return sources;
  }
  @Override
  public List<AstNode> getNodes() {
    return nodes;
  } 
  
  
  public static HdesEnvirBuilder builder() {
    return new ImmutableHdesEnvirBuilder();
  }
  
  public static class ImmutableHdesEnvirBuilder implements HdesEnvirBuilder {

    private boolean strict;
    
    @Override
    public HdesEnvirBuilder from(HdesEnvir envir) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public HdesEnvirBuilder strict() {
      strict = true;
      return this;
    }

    @Override
    public HdesEnvir build() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public SourceBuilder<HdesEnvirBuilder> add() {
      // TODO Auto-generated method stub
      return null;
    } 
  }
}

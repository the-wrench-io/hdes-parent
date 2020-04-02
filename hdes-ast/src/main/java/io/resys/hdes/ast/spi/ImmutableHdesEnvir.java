package io.resys.hdes.ast.spi;

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

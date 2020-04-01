package io.resys.hdes.ast.spi;

import io.resys.hdes.ast.api.HdesEnvir;

public class ImmutableHdesEnvir implements HdesEnvir {
  
  
  public static HdesEnvirBuilder build() {
    return new GenericHdesEnvirBuilder();
  }
  
  public static class GenericHdesEnvirBuilder implements HdesEnvirBuilder {

    private boolean strict;
    
    @Override
    public HdesEnvirBuilder from(HdesEnvir envir) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public HdesEnvirBuilder addFlow(String src) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public HdesEnvirBuilder addExpression(String src) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public HdesEnvirBuilder addManualTask(String src) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public HdesEnvirBuilder addDecisionTable(String src) {
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
    
  } 
}

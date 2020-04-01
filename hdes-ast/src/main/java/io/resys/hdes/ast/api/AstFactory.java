package io.resys.hdes.ast.api;

import io.resys.hdes.ast.api.nodes.AstNode.Envir;

public interface AstFactory {
  
  EnvirBuilder builder();
  
  interface EnvirBuilder {
    EnvirBuilder from(Envir envir);
    EnvirBuilder addFlow(String src);
    EnvirBuilder addExpression(String src);
    EnvirBuilder addManualTask(String src);
    EnvirBuilder addDecisionTable(String src);
    Envir build();
  }
}

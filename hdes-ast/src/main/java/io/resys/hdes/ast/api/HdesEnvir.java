package io.resys.hdes.ast.api;

import java.util.List;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.nodes.AstNode;

public interface HdesEnvir {
  HdesEnvirSource getSource();
  AstNode getNode();
  
  @Value.Immutable
  interface HdesEnvirSource {
    List<String> getFlows();
    List<String> getExpressions();
    List<String> getDecisionTables();
    List<String> getManualTasks();
  }
  
  interface HdesEnvirBuilder {
    HdesEnvirBuilder from(HdesEnvir envir);
    HdesEnvirBuilder addFlow(String src);
    HdesEnvirBuilder addExpression(String src);
    HdesEnvirBuilder addManualTask(String src);
    HdesEnvirBuilder addDecisionTable(String src);
    HdesEnvirBuilder strict();
    HdesEnvir build();
  }
}

package io.resys.hdes.ast.api;

import java.util.List;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.nodes.AstNode;

public interface HdesEnvir {
  Source getSource();
  List<AstNode> getNodes();
  
  @Value.Immutable
  interface Source {
    List<String> getFlows();
    List<String> getExpressions();
    List<String> getDecisionTables();
    List<String> getManualTasks();
  }
  
  interface SourceBuilder<R> {
    R flow(String src);
    R expression(String src);
    R manualTask(String src);
    R serviceTask();
    R decisionTable(String src);
  }
  
  interface ExpressionBuilder<R> {
    
  }
  
  
  interface HdesEnvirBuilder {
    HdesEnvirBuilder from(HdesEnvir envir);
    SourceBuilder<HdesEnvirBuilder> add();
    HdesEnvirBuilder strict();
    HdesEnvir build();
  }
}

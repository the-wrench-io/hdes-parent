package io.resys.hdes.ast.api;

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode;
import io.resys.hdes.ast.api.nodes.FlowNode;
import io.resys.hdes.ast.api.nodes.ManualTaskNode;

public interface AstFactory {
  AstBuilder<FlowNode> flow();
  AstBuilder<ExpressionNode> expression();
  AstBuilder<ManualTaskNode> manualTask();
  AstBuilder<DecisionTableNode> decisionTable();

  interface AstBuilder<T extends AstNode> {
    AstBuilder<T> src(String src);
    T build();
  }
}
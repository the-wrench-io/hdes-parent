package io.resys.hdes.client.spi.config;

import java.util.List;

import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;

public interface HdesClientConfig {

  List<AstFlowNodeVisitor> getFlowVisitors();
  HdesClientConfig config(AstFlowNodeVisitor ... changes);
  
  interface AstFlowNodeVisitor {
    void visit(AstFlowRoot node, ImmutableAstFlow.Builder nodesBuilder);
  }
}

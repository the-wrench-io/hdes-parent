package io.resys.hdes.ast.api;

import java.util.Collection;

import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;

public interface AstEnvir {
  Collection<BodyNode> getValues();
  BodyNode get(String id);
  
  interface Builder {
    Builder from(AstEnvir envir);
    SourceBuilder<Builder> add();
    AstEnvir build();
  }
  
  interface SourceBuilder<R> {
    SourceBuilder<R> externalId(String externalId);
    R flow(String src);
    R expression(String src, ScalarType type);
    R decisionTable(String src);
    R manualTask(String src);
  }
}

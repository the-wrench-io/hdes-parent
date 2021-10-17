package io.resys.hdes.client.spi.flow.validators;

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.api.ast.ImmutableFlowAstCommandMessage;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;

public class IdValidator implements AstFlowNodeVisitor {

  @Override
  public void visit(AstFlowRoot node, ImmutableAstFlow.Builder modelBuilder) {
    if(node.getId() == null) {
      modelBuilder.addMessages(
          ImmutableFlowAstCommandMessage.builder()
          .line(0)
          .value("flow must have id")
          .range(AstFlowNodesFactory.range().build(0, 0))
          .type(FlowCommandMessageType.ERROR)
          .build());
      return;
    }
    if(StringUtils.isEmpty(node.getId().getValue())) {
      modelBuilder.addMessages(
          ImmutableFlowAstCommandMessage.builder()
          .line(node.getId().getStart())
          .value("flow id must have a value")
          .range(AstFlowNodesFactory.range().build(0, node.getId().getSource().getValue().length()))
          .type(FlowCommandMessageType.ERROR)
          .build());
    }
  }
}

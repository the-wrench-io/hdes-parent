package io.resys.hdes.client.spi.flow.validators;

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.api.ast.ImmutableFlowAstCommandMessage;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;

public class DescriptionValidator implements AstFlowNodeVisitor {

  @Override
  public void visit(AstFlowRoot node, ImmutableAstFlow.Builder modelBuilder) {
    if(node.getDescription() == null) {
      return;
    }
    if(StringUtils.isEmpty(node.getDescription().getValue())) {
      modelBuilder.addMessages(
          ImmutableFlowAstCommandMessage.builder()
          .line(node.getDescription().getStart())
          .value("flow description must have a value")
          .range(AstFlowNodesFactory.range().build(0, node.getDescription().getSource().getValue().length()))
          .type(FlowCommandMessageType.WARNING)
          .build());
    }
  }
}

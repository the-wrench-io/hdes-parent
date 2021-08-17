package io.resys.wrench.assets.flow.spi.builders;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.flow.api.FlowAstFactory;
import io.resys.wrench.assets.flow.api.FlowAstFactory.Node;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeBuilder;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.FlowRepository.FlowNodeBuilder;
import io.resys.wrench.assets.flow.api.model.FlowAst;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandMessageType;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandType;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowCommand;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowCommandMessage;

public class GenericFlowCommandModelBuilder implements FlowNodeBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenericFlowCommandModelBuilder.class);

  private final FlowAstFactory nodeRepository;
  private final Collection<NodeFlowVisitor> visitors;
  private ArrayNode src;
  private Integer rev;

  public GenericFlowCommandModelBuilder(FlowAstFactory nodeRepository, Collection<NodeFlowVisitor> visitors) {
    super();
    this.nodeRepository = nodeRepository;
    this.visitors = visitors;
  }

  @Override
  public FlowNodeBuilder src(ArrayNode src) {
    this.src = src;
    return this;
  }

  @Override
  public FlowNodeBuilder rev(Integer rev) {
    this.rev = rev;
    return this;
  }

  @Override
  public FlowAst build() {
    Assert.notNull(src, () -> "src can't ne null!");

    ImmutableFlowAst.Builder result = ImmutableFlowAst.builder();
    NodeBuilder nodeBuilder = nodeRepository.create((message) -> result.addMessages(message));

    if(rev != null) {
      int limit = this.rev;
      int runningVersion = 0;
      for(JsonNode command : src) {
        if(runningVersion++ > limit) {
          break;
        }
        create((ObjectNode) command, nodeBuilder, result);
      }
    } else {
      src.forEach(command -> create((ObjectNode) command, nodeBuilder, result));
    }

    NodeFlow flow = nodeBuilder.build();
    try {
      visitors.stream().forEach(v -> v.visit(flow, result));
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      result.addMessages(
          ImmutableFlowCommandMessage.builder()
          .line(0)
          .value("message: " + e.getMessage())
          .type(FlowCommandMessageType.ERROR)
          .build());
    }
    
    Node id = flow.getId();
    
    return result
        .name(id == null ? "": id.getValue())
        .rev(this.rev == null ? src.size() : this.rev)
        .src(flow)
        .build();
  }

  private void create(ObjectNode command, NodeBuilder builder, ImmutableFlowAst.Builder modelBuilder) {
    int line = command.get("id").asInt();
    FlowCommandType type = FlowCommandType.valueOf(command.get("type").asText());

    String text = getText(command);
    if(type == FlowCommandType.DELETE) {
      builder.delete(line, command.get("value").asInt());
    } else if(type == FlowCommandType.ADD) {
      builder.add(line, text);
    } else {
      builder.set(line, text);
    }
    modelBuilder.addCommands(ImmutableFlowCommand.builder().id(line).value(text).type(type).build());
  }

  private String getText(ObjectNode command) {
    return command.hasNonNull("value") ? command.get("value").asText() : null;
  }
}

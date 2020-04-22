package io.resys.hdes.ast.spi.visitors.ast;

/*-
 * #%L
 * hdes-ast
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.HdesParser.EndMappingContext;
import io.resys.hdes.ast.HdesParser.FlBodyContext;
import io.resys.hdes.ast.HdesParser.MappingArgContext;
import io.resys.hdes.ast.HdesParser.MappingArgsContext;
import io.resys.hdes.ast.HdesParser.MappingContext;
import io.resys.hdes.ast.HdesParser.MappingValueContext;
import io.resys.hdes.ast.HdesParser.NextTaskContext;
import io.resys.hdes.ast.HdesParser.TaskArgsContext;
import io.resys.hdes.ast.HdesParser.TaskPointerContext;
import io.resys.hdes.ast.HdesParser.TaskRefContext;
import io.resys.hdes.ast.HdesParser.TaskTypesContext;
import io.resys.hdes.ast.HdesParser.TasksContext;
import io.resys.hdes.ast.HdesParser.ThenPointerContext;
import io.resys.hdes.ast.HdesParser.WhenThenPointerArgsContext;
import io.resys.hdes.ast.HdesParser.WhenThenPointerContext;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.DirectionType;
import io.resys.hdes.ast.api.nodes.AstNode.Headers;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.ExpressionNode.ExpressionBody;
import io.resys.hdes.ast.api.nodes.FlowNode;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.RefTaskType;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableEndPointer;
import io.resys.hdes.ast.api.nodes.ImmutableFlowBody;
import io.resys.hdes.ast.api.nodes.ImmutableFlowInputs;
import io.resys.hdes.ast.api.nodes.ImmutableFlowOutputs;
import io.resys.hdes.ast.api.nodes.ImmutableFlowTaskNode;
import io.resys.hdes.ast.api.nodes.ImmutableMapping;
import io.resys.hdes.ast.api.nodes.ImmutableTaskRef;
import io.resys.hdes.ast.api.nodes.ImmutableThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThen;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThenPointer;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentDescription;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentTypeName;
import io.resys.hdes.ast.spi.visitors.ast.util.FlowTreePointerParser;
import io.resys.hdes.ast.spi.visitors.ast.util.FlowTreePointerParser.FwRedundentOrderedTasks;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class FwParserAstNodeVisitor extends MtParserAstNodeVisitor {
  
  public FwParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super(tokenIdGenerator);
  }
  
  // Internal only
  @Value.Immutable
  public interface FwRedundentTasks extends FlowNode {
    List<FlowTaskNode> getValues();
  }  
  @Value.Immutable
  public interface FwRedundentMapping extends FlowNode {
    List<Mapping> getValues();
  }
  @Value.Immutable
  public interface FwRedundentMappingArgs extends FlowNode {
    List<Mapping> getValues();
  }    
  @Value.Immutable
  public interface FwRedundentRefTaskType extends FlowNode {
    RefTaskType getValue();
  }
  @Value.Immutable
  public interface FwRedundentMappingValue extends FlowNode {
    String getValue();
  } 

  @Override
  public FlowBody visitFlBody(FlBodyContext ctx) {
    Nodes children = nodes(ctx);
    FwRedundentTasks redundentTasks = children.of(FwRedundentTasks.class).get();
    FwRedundentOrderedTasks tasks = new FlowTreePointerParser().visit(redundentTasks);
    
    for(FlowTaskNode unclaimed : tasks.getUnclaimed()) {
      break;
    }
    
    Headers headers = children.of(Headers.class).get();
    return ImmutableFlowBody.builder()
        .inputs(ImmutableFlowInputs.builder()
            .token(headers.getToken())
            .values(headers.getValues().stream()
                .filter(t -> t.getDirection() == DirectionType.IN).collect(Collectors.toList()))
            .build())
        
        .outputs(ImmutableFlowOutputs.builder()
            .token(token(ctx))
            .values(headers.getValues().stream()
                .filter(t -> t.getDirection() == DirectionType.OUT).collect(Collectors.toList()))
            .build())
        
        .token(token(ctx))
        .id(children.of(RedundentTypeName.class).get().getValue())
        .description(children.of(RedundentDescription.class).map(e -> e.getValue()).orElse(null))
        .task(tasks.getFirst())
        .unreachableTasks(tasks.getUnclaimed())
        .build();
  }

  @Override
  public FwRedundentTasks visitTasks(TasksContext ctx) {
    return nodes(ctx).of(FwRedundentTasks.class).orElse(
        ImmutableFwRedundentTasks.builder()
        .token(token(ctx))
        .build());
  }

  @Override
  public FwRedundentTasks visitTaskArgs(TaskArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    AstNode.Token token = token(ctx);
    return ImmutableFwRedundentTasks.builder()
        .token(token)
        .values(nodes.list(FlowTaskNode.class))
        .build();
  }

  @Override
  public EndPointer visitEndMapping(EndMappingContext ctx) {
    return ImmutableEndPointer.builder()
        .token(token(ctx))
        .name("end")
        .values(nodes(ctx).of(FwRedundentMapping.class).get().getValues())
        .build();
  }

  @Override
  public FlowTaskNode visitNextTask(NextTaskContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFlowTaskNode.builder()
        .token(token(ctx))
        .id(nodes.of(RedundentTypeName.class).get().getValue())
        .next(nodes.of(FlowTaskPointer.class))
        .ref(nodes.of(TaskRef.class))
        .build();
  }
  
  @Override
  public FlowTaskPointer visitTaskPointer(TaskPointerContext ctx) {
    return (FlowTaskPointer) first(ctx);
  }
  
  @Override
  public WhenThenPointer visitWhenThenPointerArgs(WhenThenPointerArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenThenPointer.builder()
        .token(token(ctx))
        .values(nodes.list(WhenThen.class))
        .build();
  }

  @Override
  public WhenThen visitWhenThenPointer(WhenThenPointerContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenThen.builder()
        .token(token(ctx))
        .when(nodes.of(ExpressionBody.class))
        .then(nodes.of(ThenPointer.class).get())
        .build();
  }

  @Override
  public ThenPointer visitThenPointer(ThenPointerContext ctx) {
    return ImmutableThenPointer.builder()
        .token(token(ctx))
        .name(nodes(ctx).of(RedundentTypeName.class).map(e -> e.getValue()).orElse("end"))
        .build();
  }
  
  @Override
  public TaskRef visitTaskRef(TaskRefContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableTaskRef.builder()
        .token(token(ctx))
        .type(nodes.of(FwRedundentRefTaskType.class).get().getValue())
        .value(nodes.of(RedundentTypeName.class).get().getValue())
        .mapping(nodes.of(FwRedundentMapping.class).get().getValues())
        .build();
  }

  @Override
  public FwRedundentMapping visitMapping(MappingContext ctx) {
    return ImmutableFwRedundentMapping.builder()
        .token(token(ctx))
        .values(nodes(ctx).of(FwRedundentMappingArgs.class).map(e -> e.getValues()).orElse(Collections.emptyList()))
        .build();
  }

  @Override
  public FwRedundentMappingArgs visitMappingArgs(MappingArgsContext ctx) {
    return ImmutableFwRedundentMappingArgs.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(Mapping.class))
        .build();
  }

  @Override
  public Mapping visitMappingArg(MappingArgContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMapping.builder()
        .token(token(ctx))
        .left(nodes.of(RedundentTypeName.class).get().getValue())
        .right(nodes.of(FwRedundentMappingValue.class).get().getValue())
        .build();
  }

  @Override
  public FwRedundentMappingValue visitMappingValue(MappingValueContext ctx) {
    AstNode first = first(ctx);
    String value;
    if(first instanceof Literal) {
      value = ((Literal) first).getValue();
    } else if(first instanceof RedundentTypeName) {
      value = ((RedundentTypeName) first).getValue();
    } else {
      throw new AstNodeException("Unknown mapping value: " + ctx.getText() + "!");
    }
    
    return ImmutableFwRedundentMappingValue.builder()
        .token(token(ctx))
        .value(value)
        .build();
  }

  @Override
  public FwRedundentRefTaskType visitTaskTypes(TaskTypesContext ctx) {
    TerminalNode terminalNode = (TerminalNode) ctx.getChild(0);
    RefTaskType type = null;
    switch(terminalNode.getSymbol().getType()) {
    case HdesParser.MANUAL_TASK: type = RefTaskType.MANUAL_TASK; break;
    case HdesParser.FLOW_TASK: type = RefTaskType.FLOW_TASK; break;
    case HdesParser.ST_TASK: type = RefTaskType.SERVICE_TASK; break;
    case HdesParser.DT_TASK: type = RefTaskType.DECISION_TABLE; break;
    default: throw new AstNodeException("Unknown task type: " + ctx.getText() + "!");
    }
    return ImmutableFwRedundentRefTaskType.builder()
        .token(token(ctx))
        .value(type)
        .build();
  }
}

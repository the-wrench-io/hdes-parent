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
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.FlowParser;
import io.resys.hdes.ast.FlowParser.ArrayTypeContext;
import io.resys.hdes.ast.FlowParser.DebugValueContext;
import io.resys.hdes.ast.FlowParser.DescriptionContext;
import io.resys.hdes.ast.FlowParser.EndTaskContext;
import io.resys.hdes.ast.FlowParser.FlowContext;
import io.resys.hdes.ast.FlowParser.IdContext;
import io.resys.hdes.ast.FlowParser.InputArgsContext;
import io.resys.hdes.ast.FlowParser.InputContext;
import io.resys.hdes.ast.FlowParser.InputsContext;
import io.resys.hdes.ast.FlowParser.LiteralContext;
import io.resys.hdes.ast.FlowParser.MappingArgContext;
import io.resys.hdes.ast.FlowParser.MappingArgsContext;
import io.resys.hdes.ast.FlowParser.MappingContext;
import io.resys.hdes.ast.FlowParser.MappingValueContext;
import io.resys.hdes.ast.FlowParser.NextTaskContext;
import io.resys.hdes.ast.FlowParser.ObjectTypeContext;
import io.resys.hdes.ast.FlowParser.PointerContext;
import io.resys.hdes.ast.FlowParser.ScalarTypeContext;
import io.resys.hdes.ast.FlowParser.SimpleTypeContext;
import io.resys.hdes.ast.FlowParser.TaskArgsContext;
import io.resys.hdes.ast.FlowParser.TaskContext;
import io.resys.hdes.ast.FlowParser.TaskRefContext;
import io.resys.hdes.ast.FlowParser.TaskTypesContext;
import io.resys.hdes.ast.FlowParser.TasksContext;
import io.resys.hdes.ast.FlowParser.ThenContext;
import io.resys.hdes.ast.FlowParser.TypeNameContext;
import io.resys.hdes.ast.FlowParser.WhenExpressionContext;
import io.resys.hdes.ast.FlowParser.WhenThenArgsContext;
import io.resys.hdes.ast.FlowParser.WhenThenContext;
import io.resys.hdes.ast.FlowParserBaseVisitor;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.ArrayInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.InputNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.FlowNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowReturnType;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTask;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.RefTaskType;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.When;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableArrayInputNode;
import io.resys.hdes.ast.api.nodes.ImmutableFlowBody;
import io.resys.hdes.ast.api.nodes.ImmutableFlowInputs;
import io.resys.hdes.ast.api.nodes.ImmutableFlowReturnType;
import io.resys.hdes.ast.api.nodes.ImmutableFlowTask;
import io.resys.hdes.ast.api.nodes.ImmutableMapping;
import io.resys.hdes.ast.api.nodes.ImmutableObjectInputNode;
import io.resys.hdes.ast.api.nodes.ImmutableScalarInputNode;
import io.resys.hdes.ast.api.nodes.ImmutableTaskRef;
import io.resys.hdes.ast.api.nodes.ImmutableThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableWhen;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThen;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThenPointer;
import io.resys.hdes.ast.spi.visitors.ast.util.FlowTreePointerParser;
import io.resys.hdes.ast.spi.visitors.ast.util.FlowTreePointerParser.FwRedundentOrderedTasks;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class FwParserAstNodeVisitor extends FlowParserBaseVisitor<AstNode> {
  private final TokenIdGenerator tokenIdGenerator;

  public FwParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
  }
  
  // Internal only
  @Value.Immutable
  public interface FwRedundentId extends FlowNode {
    String getValue();
  }
  @Value.Immutable
  public interface FwRedundentDescription extends FlowNode {
    String getValue();
  }
  @Value.Immutable
  public interface FwRedundentTypeName extends FlowNode {
    String getValue();
  }
  @Value.Immutable
  public interface FwRedundentInputArgs extends FlowNode {
    List<InputNode> getValues();
  }
  @Value.Immutable
  public interface FwRedundentScalarType extends FlowNode {
    ScalarType getValue();
  }
  @Value.Immutable
  public interface FwRedundentDebugValue extends FlowNode {
    String getValue();
  }  
  @Value.Immutable
  public interface FwRedundentTasks extends FlowNode {
    List<FlowTask> getValues();
    FlowReturnType getReturnType();
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
  public AstNode visitLiteral(LiteralContext ctx) {
    return Nodes.literal(ctx, token(ctx));
  }

  @Override
  public FlowBody visitFlow(FlowContext ctx) {
    Nodes children = nodes(ctx);
    FwRedundentTasks redundentTasks = children.of(FwRedundentTasks.class).get();
    FwRedundentOrderedTasks tasks = new FlowTreePointerParser().visit(redundentTasks);
    
    return ImmutableFlowBody.builder()
        .token(token(ctx))
        .id(children.of(FwRedundentId.class).get().getValue())
        .inputs(children.of(FlowInputs.class).get())
        .description(children.of(FwRedundentDescription.class).map(e -> e.getValue()).orElse(null))
        .returnType(redundentTasks.getReturnType())
        .task(tasks.getFirst())
        .unreachableTasks(tasks.getUnclaimed())
        .build();
  }

  @Override
  public FlowInputs visitInputs(InputsContext ctx) {
    List<InputNode> values = nodes(ctx).of(FwRedundentInputArgs.class)
        .map(a -> a.getValues()).orElse(Collections.emptyList());
    return ImmutableFlowInputs.builder()
        .token(token(ctx))
        .values(values)
        .build();
  }

  @Override
  public FwRedundentInputArgs visitInputArgs(InputArgsContext ctx) {
    return ImmutableFwRedundentInputArgs.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(InputNode.class))
        .build();
  }
  
  @Override
  public AstNode visitInput(InputContext ctx) {
    ParseTree c = ctx.getChild(1);
    return c.accept(this);
  }

  @Override
  public FwRedundentDebugValue visitDebugValue(DebugValueContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFwRedundentDebugValue.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }
  
  @Override
  public FwRedundentScalarType visitScalarType(ScalarTypeContext ctx) {
    return ImmutableFwRedundentScalarType.builder()
        .token(token(ctx))
        .value(ScalarType.valueOf(ctx.getText()))
        .build();
  }

  @Override
  public ScalarInputNode visitSimpleType(SimpleTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableScalarInputNode.builder()
        .token(token(ctx))
        .required(isRequiredInputType(ctx))
        .type(nodes.of(FwRedundentScalarType.class).get().getValue())
        .name(nodes.of(FwRedundentTypeName.class).get().getValue())
        .debugValue(nodes.of(FwRedundentDebugValue.class).map(e -> e.getValue()))
        .build();
  }
  
  @Override
  public ArrayInputNode visitArrayType(ArrayTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableArrayInputNode.builder()
        .token(token(ctx))
        .required(isRequiredInputType(ctx))
        .value(nodes.of(InputNode.class).get())
        .build();
  }

  @Override
  public ObjectInputNode visitObjectType(ObjectTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    List<InputNode> values = nodes.of(FlowInputs.class).map((FlowInputs i)-> i.getValues())
        .orElse(Collections.emptyList());
    
    return ImmutableObjectInputNode.builder()
        .token(token(ctx))
        .required(isRequiredInputType(ctx))
        .name(nodes.of(FwRedundentTypeName.class).get().getValue())
        .values(values)
        .build();
  }
  
  @Override
  public FwRedundentTasks visitTasks(TasksContext ctx) {
    AstNode.Token token = token(ctx);
    return nodes(ctx).of(FwRedundentTasks.class).orElse(
        ImmutableFwRedundentTasks.builder()
        .token(token(ctx))
        .returnType(ImmutableFlowReturnType.builder().token(token).build())
        .build());
  }

  @Override
  public FwRedundentTasks visitTaskArgs(TaskArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    AstNode.Token token = token(ctx);
    return ImmutableFwRedundentTasks.builder()
        .token(token)
        .values(nodes.list(FlowTask.class))
        .returnType(nodes.of(FlowReturnType.class).orElse(ImmutableFlowReturnType.builder().token(token).build()))
        .build();
  }

  @Override
  public AstNode visitTask(TaskContext ctx) {
    return first(ctx);
  }

  @Override
  public FlowReturnType visitEndTask(EndTaskContext ctx) {
    return ImmutableFlowReturnType.builder()
        .token(token(ctx))
        .mapping(nodes(ctx).of(FwRedundentMapping.class).get().getValues())
        .build();
  }

  @Override
  public FlowTask visitNextTask(NextTaskContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFlowTask.builder()
        .token(token(ctx))
        .id(nodes.of(FwRedundentTypeName.class).get().getValue())
        .next(nodes.of(FlowTaskPointer.class))
        .ref(nodes.of(TaskRef.class))
        .build();
  }
  
  @Override
  public FlowTaskPointer visitPointer(PointerContext ctx) {
    return (FlowTaskPointer) first(ctx);
  }
  
  @Override
  public WhenThenPointer visitWhenThenArgs(WhenThenArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenThenPointer.builder()
        .token(token(ctx))
        .values(nodes.list(WhenThen.class))
        .build();
  }

  @Override
  public WhenThen visitWhenThen(WhenThenContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenThen.builder()
        .token(token(ctx))
        .when(nodes.of(When.class).get())
        .then(nodes.of(ThenPointer.class).get())
        .build();
  }

  @Override
  public When visitWhenExpression(WhenExpressionContext ctx) {
    return ImmutableWhen.builder()
        .token(token(ctx))
        .node(Optional.empty())
        .text(Nodes.getStringLiteralValue(ctx))
        .build();
  }
  
  @Override
  public ThenPointer visitThen(ThenContext ctx) {
    return ImmutableThenPointer.builder()
        .token(token(ctx))
        .name(nodes(ctx).of(FwRedundentTypeName.class).get().getValue())
        .build();
  }

  @Override
  public TaskRef visitTaskRef(TaskRefContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableTaskRef.builder()
        .token(token(ctx))
        .type(nodes.of(FwRedundentRefTaskType.class).get().getValue())
        .value(nodes.of(FwRedundentTypeName.class).get().getValue())
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
        .left(nodes.of(FwRedundentTypeName.class).get().getValue())
        .right(nodes.of(FwRedundentMappingValue.class).get().getValue())
        .build();
  }

  @Override
  public FwRedundentMappingValue visitMappingValue(MappingValueContext ctx) {
    AstNode first = first(ctx);
    String value;
    if(first instanceof Literal) {
      value = ((Literal) first).getValue();
    } else if(first instanceof FwRedundentTypeName) {
      value = ((FwRedundentTypeName) first).getValue();
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
    case FlowParser.MANUAL_TASK: type = RefTaskType.MANUAL_TASK; break;
    case FlowParser.FLOW_TASK: type = RefTaskType.FLOW_TASK; break;
    case FlowParser.ST_TASK: type = RefTaskType.SERVICE_TASK; break;
    case FlowParser.DT_TASK: type = RefTaskType.DECISION_TABLE; break;
    default: throw new AstNodeException("Unknown task type: " + ctx.getText() + "!");
    }
    return ImmutableFwRedundentRefTaskType.builder()
        .token(token(ctx))
        .value(type)
        .build();
  }

  @Override
  public FwRedundentId visitId(IdContext ctx) {
    return ImmutableFwRedundentId.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(FwRedundentTypeName.class).get().getValue())
        .build();
  }

  @Override
  public FwRedundentDescription visitDescription(DescriptionContext ctx) {
    return ImmutableFwRedundentDescription.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(Literal.class).get().getValue())
        .build();
  }
  @Override
  public FwRedundentTypeName visitTypeName(TypeNameContext ctx) {
    return ImmutableFwRedundentTypeName.builder()
        .token(token(ctx))
        .value(ctx.getText())
        .build();
  }
  
  private boolean isRequiredInputType(ParserRuleContext ctx) {
    TerminalNode requirmentType = (TerminalNode) ctx.getParent().getChild(0);
    return requirmentType.getSymbol().getType() == FlowParser.REQUIRED;
  }
  
  private AstNode first(ParserRuleContext ctx) {
    ParseTree c = ctx.getChild(0);
    return c.accept(this);
  }

  private Nodes nodes(ParserRuleContext node) {
    return Nodes.from(node, this);
  }

  private AstNode.Token token(ParserRuleContext node) {
    return Nodes.token(node, tokenIdGenerator);
  }
}

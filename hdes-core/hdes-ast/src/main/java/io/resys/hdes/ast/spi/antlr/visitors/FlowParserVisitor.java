package io.resys.hdes.ast.spi.antlr.visitors;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.HdesParser.CallActionContext;
import io.resys.hdes.ast.HdesParser.CallDefContext;
import io.resys.hdes.ast.HdesParser.ContinuePointerContext;
import io.resys.hdes.ast.HdesParser.EndAsPointerContext;
import io.resys.hdes.ast.HdesParser.FastMappingContext;
import io.resys.hdes.ast.HdesParser.FieldMappingContext;
import io.resys.hdes.ast.HdesParser.FindFirstContext;
import io.resys.hdes.ast.HdesParser.FlBodyContext;
import io.resys.hdes.ast.HdesParser.IterateActionContext;
import io.resys.hdes.ast.HdesParser.MappingArgContext;
import io.resys.hdes.ast.HdesParser.MappingContext;
import io.resys.hdes.ast.HdesParser.MappingValueContext;
import io.resys.hdes.ast.HdesParser.PointerContext;
import io.resys.hdes.ast.HdesParser.SortByArgContext;
import io.resys.hdes.ast.HdesParser.SortByContext;
import io.resys.hdes.ast.HdesParser.StepContext;
import io.resys.hdes.ast.HdesParser.StepsContext;
import io.resys.hdes.ast.HdesParser.ThenPointerContext;
import io.resys.hdes.ast.HdesParser.WhenThenPointerArgsContext;
import io.resys.hdes.ast.HdesParser.WhenThenPointerContext;
import io.resys.hdes.ast.api.HdesException;
import io.resys.hdes.ast.api.nodes.BodyNode.Headers;
import io.resys.hdes.ast.api.nodes.ExpressionNode.ExpressionBody;
import io.resys.hdes.ast.api.nodes.FlowNode;
import io.resys.hdes.ast.api.nodes.FlowNode.CallAction;
import io.resys.hdes.ast.api.nodes.FlowNode.CallDef;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.SortBy;
import io.resys.hdes.ast.api.nodes.FlowNode.SortByDef;
import io.resys.hdes.ast.api.nodes.FlowNode.SplitPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Step;
import io.resys.hdes.ast.api.nodes.FlowNode.StepAction;
import io.resys.hdes.ast.api.nodes.FlowNode.StepPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenPointer;
import io.resys.hdes.ast.api.nodes.HdesNode;
import io.resys.hdes.ast.api.nodes.HdesNode.ErrorNode;
import io.resys.hdes.ast.api.nodes.HdesTree;
import io.resys.hdes.ast.api.nodes.ImmutableBodyId;
import io.resys.hdes.ast.api.nodes.ImmutableCallAction;
import io.resys.hdes.ast.api.nodes.ImmutableCallDef;
import io.resys.hdes.ast.api.nodes.ImmutableEmptyAction;
import io.resys.hdes.ast.api.nodes.ImmutableEndPointer;
import io.resys.hdes.ast.api.nodes.ImmutableErrorNode;
import io.resys.hdes.ast.api.nodes.ImmutableExpressionMappingDef;
import io.resys.hdes.ast.api.nodes.ImmutableFastMappingDef;
import io.resys.hdes.ast.api.nodes.ImmutableFieldMappingDef;
import io.resys.hdes.ast.api.nodes.ImmutableFlowBody;
import io.resys.hdes.ast.api.nodes.ImmutableIterationEndPointer;
import io.resys.hdes.ast.api.nodes.ImmutableObjectMappingDef;
import io.resys.hdes.ast.api.nodes.ImmutableSimpleInvocation;
import io.resys.hdes.ast.api.nodes.ImmutableSortBy;
import io.resys.hdes.ast.api.nodes.ImmutableSortByDef;
import io.resys.hdes.ast.api.nodes.ImmutableSplitPointer;
import io.resys.hdes.ast.api.nodes.ImmutableStep;
import io.resys.hdes.ast.api.nodes.ImmutableWhenPointer;
import io.resys.hdes.ast.api.nodes.InvocationNode;
import io.resys.hdes.ast.api.nodes.InvocationNode.SimpleInvocation;
import io.resys.hdes.ast.api.nodes.MappingNode.FastMappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.FieldMappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.MappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.ObjectMappingDef;
import io.resys.hdes.ast.spi.ImmutableHdesTree;
import io.resys.hdes.ast.spi.ImmutableRootNode;
import io.resys.hdes.ast.spi.antlr.util.Nodes;
import io.resys.hdes.ast.spi.antlr.visitors.FlowStepParserVisitor.StepTree;

public class FlowParserVisitor extends ServiceTaskParserVisitor {
  
  @Value.Immutable
  public interface FwRedundentSteps extends FlowNode {
    List<Step> getValues();
  }
  
  @Value.Immutable
  public interface FwRedundentFindFirst extends FlowNode {
  }
  
  @Value.Immutable
  interface FwRedundentThenPointer extends StepPointer {
    SimpleInvocation getId();
  }
  
  @Value.Immutable
  interface RedundentIterateAction extends StepAction {
    InvocationNode getOver();
    Optional<SortBy> getSortBy();
    Boolean getFindFirst();
    List<Step> getSteps();
  }
  
  @Value.Immutable
  interface FwRedundentEmptyPointer extends StepPointer { 
    
  }
  @Value.Immutable
  interface FwRedundentContinuePointer extends StepPointer { 
    
  }  
  
  @Override
  public FlowBody visitFlBody(FlBodyContext ctx) {
    Nodes children = nodes(ctx);
    Headers headers = children.of(Headers.class).get();
    SimpleInvocation id = children.of(SimpleInvocation.class).get();
    FwRedundentSteps steps = children.of(FwRedundentSteps.class).get();
    
    ImmutableFlowBody body = ImmutableFlowBody.builder()
        .token(children.getToken())
        .id(ImmutableBodyId.builder().token(id.getToken()).value(id.getValue()).build())
        .headers(headers)
        .build();
    
    ImmutableRootNode root = new ImmutableRootNode(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    HdesTree hdesCtx = ImmutableHdesTree.builder().root(root).next(body);
    StepTree tree = FlowStepParserVisitor.builder().ctx(hdesCtx).src(steps.getValues()).build();
    
    if(!tree.getUnclaimed().isEmpty()) {
      List<ErrorNode> errors = new ArrayList<>();
      for(Step unclaimed : tree.getUnclaimed()) {
        ImmutableErrorNode.builder()
        .target(unclaimed)
        .bodyId(body.getId().getValue())
        .message("Can't find step defined in then: " + unclaimed.getId().getValue() + "!")
        .build();
      }
      throw new HdesException(errors);
    }
    
    return body.withStep(tree.getStep());
  }
  
  @Override
  public FwRedundentSteps visitSteps(StepsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFwRedundentSteps.builder()
        .token(nodes.getToken())
        .values(nodes.list(Step.class))
        .build();
  }
  
  @Override
  public Step visitStep(StepContext ctx) {
    Nodes nodes = nodes(ctx);
    SimpleInvocation id = nodes.of(SimpleInvocation.class).get();
    StepAction action = nodes.of(StepAction.class).orElseGet(() -> ImmutableEmptyAction.builder().token(nodes.getToken()).build());
    StepPointer pointer = nodes.of(StepPointer.class).orElseGet(() -> ImmutableFwRedundentEmptyPointer.builder().token(nodes.getToken()).build());
    
    boolean await = false;
    if(action instanceof CallAction) {
      CallAction call = (CallAction) action;
      await = call.getCalls().stream().filter(c -> c.getAwait()).findFirst().map(c -> true).orElse(false);
    }
    return ImmutableStep.builder().token(nodes.getToken()).id(id).action(action).pointer(pointer).await(await).build();
  }
  
  @Override
  public RedundentIterateAction visitIterateAction(IterateActionContext ctx) {
    final var nodes = nodes(ctx);
    final var token = token(ctx);
    
    List<FlowNode.Step> steps = nodes.of(FwRedundentSteps.class)
        .map(e -> e.getValues())
        .orElseGet(() -> { 
          var action = nodes.of(StepAction.class).orElseGet(() -> ImmutableEmptyAction.builder().token(nodes.getToken()).build());
          var pointer = nodes.of(StepPointer.class).orElseGet(() -> ImmutableIterationEndPointer.builder().token(nodes.getToken()).build());
          
          boolean await = false;
          if(action instanceof CallAction) {
            CallAction call = (CallAction) action;
            await = call.getCalls().stream().filter(c -> c.getAwait()).findFirst().map(c -> true).orElse(false);
          }
          var id = ImmutableSimpleInvocation.builder().token(token).value("").build();
          return Arrays.asList(ImmutableStep.builder().token(token).id(id).action(action).pointer(pointer).await(await).build());
          
        });
    
    return ImmutableRedundentIterateAction.builder()
        .token(token)
        .over(nodes.of(InvocationNode.class).get())
        .steps(steps)
        .sortBy(nodes.of(SortBy.class))
        .findFirst(nodes.of(FwRedundentFindFirst.class).isPresent())
        .build();
  }
  
  @Override
  public SortBy visitSortBy(SortByContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableSortBy.builder().token(nodes.getToken()).values(nodes.list(SortByDef.class)).build();
  }
  
  @Override
  public SortByDef visitSortByArg(SortByArgContext ctx) {
    Nodes nodes = nodes(ctx);
    
    final boolean asc;
    if(ctx.getChildCount() > 1) {
      TerminalNode node = (TerminalNode) ctx.getChild(1);
      asc = node.getSymbol().getType() == HdesParser.ASC;
    } else {
      asc = true;
    }
    return ImmutableSortByDef.builder().token(nodes.getToken()).name(nodes.of(InvocationNode.class).get()).asc(asc).build();
  }
  
  @Override
  public HdesNode visitFindFirst(FindFirstContext ctx) {
    return ImmutableFwRedundentFindFirst.builder().token(token(ctx)).build();
  }
  
  @Override
  public CallAction visitCallAction(CallActionContext ctx) {
    Nodes nodes = nodes(ctx);
    
    List<CallDef> src = nodes.list(CallDef.class);
    List<CallDef> target = new ArrayList<>();
    
    if(src.size() > 1) {
      int index = 0;
      for(CallDef def : src) {
        target.add(ImmutableCallDef.builder().from(def).index(index++).build());
      }
    } else {
      target.addAll(src);
    }
    
    return ImmutableCallAction.builder().token(nodes.getToken()).calls(target).build();
  }
  
  @Override
  public CallDef visitCallDef(CallDefContext ctx) {
    Nodes nodes = nodes(ctx);
    TerminalNode type = (TerminalNode) ctx.getChild(0);
    
    return ImmutableCallDef.builder().token(nodes.getToken())
        .id(nodes.of(SimpleInvocation.class).get())
        .mapping(nodes.of(ObjectMappingDef.class).get())
        .await(type.getSymbol().getType() == HdesParser.AWAIT)
        .build();
  }
  
  @Override
  public StepPointer visitPointer(PointerContext ctx) {
    if(ctx.getChildCount() == 0) {
      Nodes nodes = nodes(ctx);
      return ImmutableFwRedundentEmptyPointer.builder().token(nodes.getToken()).build();
    }
    return (StepPointer) first(ctx);
  }
  
  @Override
  public StepPointer visitContinuePointer(ContinuePointerContext ctx) { 
    Nodes nodes = nodes(ctx);
    return ImmutableFwRedundentContinuePointer.builder().token(nodes.getToken()).build();
  }
  
  @Override
  public SplitPointer visitWhenThenPointerArgs(WhenThenPointerArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableSplitPointer.builder().token(nodes.getToken()).values(nodes.list(StepPointer.class)).build();
  }
  
  @Override
  public WhenPointer visitWhenThenPointer(WhenThenPointerContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenPointer.builder()
        .token(nodes.getToken())
        .when(nodes.of(ExpressionBody.class).get())
        .then(nodes.of(StepPointer.class).get())
        .build();
  }
  
  @Override
  public StepPointer visitThenPointer(ThenPointerContext ctx) {
    Nodes nodes = nodes(ctx);
    
    Optional<FwRedundentContinuePointer> next = nodes.of(FwRedundentContinuePointer.class);
    if(next.isPresent()) {
      return next.get();
    }
    
    Optional<SimpleInvocation> invocation = nodes.of(SimpleInvocation.class);
    if(invocation.isPresent()) {
      return ImmutableFwRedundentThenPointer.builder().token(nodes.getToken()).id(invocation.get()).build();
    }
    
    return nodes.of(EndPointer.class).get();
  }
  
  @Override
  public EndPointer visitEndAsPointer(EndAsPointerContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableEndPointer.builder().token(nodes.getToken()).mapping(nodes.of(ObjectMappingDef.class).get()).build();
  }
  
  @Override
  public ObjectMappingDef visitMapping(MappingContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableObjectMappingDef.builder()
        .token(nodes.getToken())
        .values(nodes.list(MappingDef.class))
        .build();
  }
  
  @Override
  public MappingDef visitMappingArg(MappingArgContext ctx) {
    return (MappingDef) first(ctx);
  }
  
  @Override
  public FieldMappingDef visitFieldMapping(FieldMappingContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFieldMappingDef.builder()
        .token(nodes.getToken())
        .left(nodes.of(SimpleInvocation.class).get())
        .right(nodes.of(MappingDef.class).get())
        .build();
  }
  
  @Override
  public FastMappingDef visitFastMapping(FastMappingContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFastMappingDef.builder()
        .token(nodes.getToken())
        .value(nodes.of(InvocationNode.class).get())
        .build();
  }
  
  @Override
  public MappingDef visitMappingValue(MappingValueContext ctx) {
    Nodes nodes = nodes(ctx);
    Optional<MappingDef> def = nodes.of(MappingDef.class);
    if(def.isPresent()) {
      return def.get();
    }
    return ImmutableExpressionMappingDef.builder()
        .token(nodes.getToken())
        .value(nodes.of(ExpressionBody.class).get())
        .build();
  }
}

package io.resys.hdes.ast.spi.antlr.visitors.returntype.flowstep;

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

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.HdesException;
import io.resys.hdes.ast.api.nodes.BodyNode.ContextTypeDef;
import io.resys.hdes.ast.api.nodes.BodyNode.ObjectDef;
import io.resys.hdes.ast.api.nodes.BodyNode.ScalarDef;
import io.resys.hdes.ast.api.nodes.BodyNode.TypeDef;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.FlowNode.CallAction;
import io.resys.hdes.ast.api.nodes.FlowNode.CallDef;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.IterateAction;
import io.resys.hdes.ast.api.nodes.FlowNode.IterationEndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.SplitPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Step;
import io.resys.hdes.ast.api.nodes.FlowNode.StepAction;
import io.resys.hdes.ast.api.nodes.FlowNode.StepPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenPointer;
import io.resys.hdes.ast.api.nodes.HdesNode;
import io.resys.hdes.ast.api.nodes.HdesTree;
import io.resys.hdes.ast.api.nodes.ImmutableErrorNode;
import io.resys.hdes.ast.api.nodes.ImmutableObjectDef;
import io.resys.hdes.ast.api.nodes.ImmutableScalarDef;
import io.resys.hdes.ast.api.nodes.ImmutableStepCallDef;
import io.resys.hdes.ast.api.visitors.FlowBodyVisitor.FlowStepVisitor;
import io.resys.hdes.ast.spi.antlr.visitors.returntype.flowstep.FlowStepDefVisitor.FlowStepDefVisitorSpec;

public class FlowStepDefVisitor implements FlowStepVisitor<FlowStepDefVisitorSpec, FlowStepDefVisitorSpec> {

  public interface FlowStepDefVisitorSpec {}
  
  @Value.Immutable
  public interface MultipleFlowStepDefVisitorSpec extends FlowStepDefVisitorSpec {
    List<TypeDef> getValues();
  }
  
  @Value.Immutable
  public interface SingleFlowStepDefVisitorSpec extends FlowStepDefVisitorSpec {
    Optional<TypeDef> getValue();
  }
  
  @Override
  public SingleFlowStepDefVisitorSpec visitBody(Step step, HdesTree ctx) {
    final var next = ctx.next(step);
    return ImmutableSingleFlowStepDefVisitorSpec.builder()
        .value(ImmutableObjectDef.builder()
            .token(step.getToken())
            .name(step.getId().getValue())
            .required(true)
            .array(false)
            .context(ContextTypeDef.STEP_RETURNS)
            .addAllValues(visitAction(step.getAction(), next).getValues())
            .build())
        .build();
  }

  @Override
  public MultipleFlowStepDefVisitorSpec visitAction(StepAction action, HdesTree ctx) {
    if(action instanceof CallAction) {
      return visitCallAction((CallAction) action, ctx);
    } else if(action instanceof IterateAction) {
      return visitIterateAction((IterateAction) action, ctx);
    }
    return ImmutableMultipleFlowStepDefVisitorSpec.builder().build();
    
  }

  @Override
  public MultipleFlowStepDefVisitorSpec visitCallAction(CallAction action, HdesTree ctx) {
    final var next = ctx.next(action);
    final var result = ImmutableMultipleFlowStepDefVisitorSpec.builder();
    
    if (action.getCalls().size() > 1) {
      int index = 0;
      for (var call : action.getCalls()) {
        result.addValues(ImmutableStepCallDef.builder().name("_" + index).index(index).callDef(call)
            .token(call.getToken()).required(false).array(false).context(ContextTypeDef.STEP_CALL)
            .values(visitCallDef(call, next).getValues()).build());
        index++;
      }
    } else if (action.getCalls().size() == 1) {
      CallDef call = action.getCalls().get(0);
      result.addValues(
          ImmutableStepCallDef.builder().token(call.getToken()).name("_").index(0).callDef(call).required(false)
              .array(false).context(ContextTypeDef.STEP_CALL).values(visitCallDef(call, next).getValues()).build());
    }
    
    return result.build();
  }

  @Override
  public MultipleFlowStepDefVisitorSpec visitCallDef(CallDef def, HdesTree ctx) {
    final var dependency = ctx.getRoot().getBody(def.getId().getValue());
    final var result = ImmutableMultipleFlowStepDefVisitorSpec.builder();
    if(dependency instanceof DecisionTableBody) {
      DecisionTableBody dt = (DecisionTableBody) dependency;
      if(dt.getHitPolicy() instanceof HitPolicyAll) {
        result.addValues(ImmutableObjectDef.builder()
            .name("_")
            .array(true)
            .values(dependency.getHeaders().getReturnDefs())
            .build());
      }
      result.addAllValues(dependency.getHeaders().getReturnDefs());      
    } else {
      result.addAllValues(dependency.getHeaders().getReturnDefs());
    }
    return result.build();
  }

  @Override
  public MultipleFlowStepDefVisitorSpec visitIterateAction(IterateAction action, HdesTree ctx) {
    TypeDef iterator = ctx.returns().build(action.getOver()).getReturns();
    
    if(!iterator.getArray()) {
      throw new HdesException(ImmutableErrorNode.builder()
          .bodyId(ctx.get().body().getId().getValue())
          .target(action.getOver())
          .message("Type def: '" + iterator.getName() + "' is NOT ARRAY, can't iterate over non array!")
          .build());
    }

    // Add iterator 
    final var result = ImmutableMultipleFlowStepDefVisitorSpec.builder();
    if(iterator instanceof ObjectDef) {
      result.addValues(ImmutableObjectDef.builder().from(iterator).context(ContextTypeDef.STEP_ITERATOR).array(false).name("_").build());
    } else if(iterator instanceof ScalarDef) {
      result.addValues(ImmutableScalarDef.builder().from(iterator).context(ContextTypeDef.STEP_ITERATOR).array(false).name("_").build());
    }
    
    // Add how the iteration ends
    Optional<ObjectDef> endAs = ctx.step().findEnd(action.getStep());
    endAs.ifPresent(e -> result.addValues(e));
    
    return result.build();
  }

  @Override
  public SingleFlowStepDefVisitorSpec visitPointer(StepPointer pointer, HdesTree ctx) {
    if(pointer instanceof EndPointer) {
      return visitEndPointer((EndPointer) pointer, ctx);
    } else if(pointer instanceof SplitPointer) {
      return visitSplitPointer((SplitPointer) pointer, ctx);
    } else if(pointer instanceof WhenPointer) {
      return visitWhenPointer((WhenPointer) pointer, ctx);
    } else if(pointer instanceof ThenPointer) {
      return visitThenPointer((ThenPointer) pointer, ctx); 
    } else if(pointer instanceof IterationEndPointer) {
      return visitIterationEndPointer((IterationEndPointer) pointer, ctx);
    }
    throw new HdesException(unknownAst(pointer));
  }

  @Override
  public SingleFlowStepDefVisitorSpec visitSplitPointer(SplitPointer pointer, HdesTree ctx) {
    final var next = ctx.next(pointer);
    for(var p : pointer.getValues()) {
      var result = visitPointer(p, next);
      if(result.getValue().isPresent()) {
        return result;
      }
    }
    return ImmutableSingleFlowStepDefVisitorSpec.builder().build();
  }

  @Override
  public SingleFlowStepDefVisitorSpec visitWhenPointer(WhenPointer pointer, HdesTree ctx) {
    final var next = ctx.next(pointer);
    return visitPointer(pointer.getThen(), next);
  }

  @Override
  public SingleFlowStepDefVisitorSpec visitThenPointer(ThenPointer pointer, HdesTree ctx) {
    final var next = ctx.next(pointer);
    return visitBody(pointer.getStep(), next);
  }

  @Override
  public SingleFlowStepDefVisitorSpec visitEndPointer(EndPointer pointer, HdesTree ctx) {
    return ImmutableSingleFlowStepDefVisitorSpec.builder().build();
  }
  
  @Override
  public SingleFlowStepDefVisitorSpec visitIterationEndPointer(IterationEndPointer pointer, HdesTree ctx) {
    return ImmutableSingleFlowStepDefVisitorSpec.builder().build();
  }
  
  private String unknownAst(HdesNode ast) {
    return new StringBuilder()
        .append("Unknown AST: ").append(ast.getClass())
        .append("  - ").append(ast).append(System.lineSeparator())
        .toString();
  }
}

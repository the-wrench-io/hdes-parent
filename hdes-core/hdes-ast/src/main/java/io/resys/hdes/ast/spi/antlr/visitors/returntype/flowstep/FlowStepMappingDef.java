package io.resys.hdes.ast.spi.antlr.visitors.returntype.flowstep;

import io.resys.hdes.ast.api.HdesException;
import io.resys.hdes.ast.api.nodes.BodyNode.ContextTypeDef;
import io.resys.hdes.ast.api.nodes.BodyNode.ObjectDef;
import io.resys.hdes.ast.api.nodes.BodyNode.ScalarDef;
import io.resys.hdes.ast.api.nodes.BodyNode.TypeDef;
import io.resys.hdes.ast.api.nodes.FlowNode.CallDef;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.StepAs;
import io.resys.hdes.ast.api.nodes.HdesNode;
import io.resys.hdes.ast.api.nodes.HdesTree;
import io.resys.hdes.ast.api.nodes.ImmutableObjectDef;
import io.resys.hdes.ast.api.nodes.ImmutableScalarDef;
import io.resys.hdes.ast.api.nodes.MappingNode.ExpressionMappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.FastMappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.FieldMappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.MappingDef;
import io.resys.hdes.ast.api.nodes.MappingNode.ObjectMappingDef;
import io.resys.hdes.ast.api.visitors.FlowBodyVisitor.FlowMappingDefVisitor;
import io.resys.hdes.ast.api.visitors.FlowBodyVisitor.MappingEvent;

public class FlowStepMappingDef implements FlowMappingDefVisitor<TypeDef, TypeDef> {

  @Override
  public ObjectDef visitBody(EndPointer node, HdesTree ctx) {
    final var builder = ImmutableObjectDef.builder()
        .name("end-as")
        .token(node.getToken()).required(true).array(false)
        .context(ContextTypeDef.STEP_END);
    
    final var next = ctx.next(node);
    for(var mapping : node.getMapping().getValues()) {
      builder.addValues(visitMappingDef(mapping, next));
    }
    return builder.build();
  }

  @Override
  public TypeDef visitMappingDef(MappingDef node, HdesTree ctx) {
    if(node instanceof ExpressionMappingDef) {
      return visitExpressionMappingDef((ExpressionMappingDef) node, ctx);
    } else if(node instanceof FastMappingDef) {
      return visitFastMappingDef((FastMappingDef) node, ctx);
    } else if(node instanceof FieldMappingDef) {
      return visitFieldMappingDef((FieldMappingDef) node, ctx);
    } else if(node instanceof ObjectMappingDef) {
      return visitObjectMappingDef((ObjectMappingDef) node, ctx);
    }
    throw new HdesException(unknownAst(node)); 
  }

  @Override
  public TypeDef visitExpressionMappingDef(ExpressionMappingDef node, HdesTree ctx) {
    return ctx.returns().build(node.getValue()).getReturns();
  }

  @Override
  public TypeDef visitFastMappingDef(FastMappingDef node, HdesTree ctx) {
    return ctx.returns().build(node.getValue()).getReturns();
  }

  @Override
  public TypeDef visitFieldMappingDef(FieldMappingDef node, HdesTree ctx) {
    TypeDef right = visitMappingDef(node.getRight(), ctx.next(node));
    if(right instanceof ScalarDef) {
      return ImmutableScalarDef.builder().from(right).name(node.getLeft().getValue()).build();
    } else if(right instanceof ObjectDef) {
      return ImmutableObjectDef.builder().from(right).name(node.getLeft().getValue()).build();
    }
    throw new HdesException(unknownAst(node));
  }

  @Override
  public ObjectDef visitObjectMappingDef(ObjectMappingDef node, HdesTree ctx) {
    final var next = ctx.next(node);
    final var builder = ImmutableObjectDef.builder();
    for(var mapping : node.getValues()) {
      builder.addValues(visitMappingDef(mapping, next));
    }
    return builder.build();
  }
  
  @Override
  public TypeDef visitBody(CallDef def, HdesTree ctx) {
    throw new HdesException(unknownAst(def));
  }
  
  @Override
  public TypeDef visitBody(CallDef def, MappingEvent event, HdesTree ctx) {
    throw new HdesException(unknownAst(def));
  }
  
  @Override
  public TypeDef visitBody(StepAs def, HdesTree ctx) {
    throw new HdesException(unknownAst(def));
  }
  
  private String unknownAst(HdesNode ast) {
    return new StringBuilder()
        .append("Unknown AST: ").append(ast.getClass())
        .append("  - ").append(ast).append(System.lineSeparator())
        .toString();
  }
}

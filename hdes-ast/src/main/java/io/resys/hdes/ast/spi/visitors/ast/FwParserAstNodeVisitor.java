package io.resys.hdes.ast.spi.visitors.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.immutables.value.Value;

import io.resys.hdes.ast.FlowParser.ConditionalThenContext;
import io.resys.hdes.ast.FlowParser.DebugValueContext;
import io.resys.hdes.ast.FlowParser.DescriptionContext;
import io.resys.hdes.ast.FlowParser.EndMappingContext;
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
import io.resys.hdes.ast.FlowParser.TaskArgsContext;
import io.resys.hdes.ast.FlowParser.TaskBodyContext;
import io.resys.hdes.ast.FlowParser.TaskContext;
import io.resys.hdes.ast.FlowParser.TaskRefContext;
import io.resys.hdes.ast.FlowParser.TasksContext;
import io.resys.hdes.ast.FlowParser.ThenContext;
import io.resys.hdes.ast.FlowParser.TypeNameContext;
import io.resys.hdes.ast.FlowParser.WhenThenArgsContext;
import io.resys.hdes.ast.FlowParser.WhenThenContext;
import io.resys.hdes.ast.FlowParserBaseVisitor;
import io.resys.hdes.ast.ManualTaskParser.DataTypeContext;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.FlowNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInput;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.ImmutableFlowBody;
import io.resys.hdes.ast.api.nodes.ImmutableFlowInputs;
import io.resys.hdes.ast.spi.visitors.ast.DtParserAstNodeVisitor.DtRedundentTypeName;
import io.resys.hdes.ast.spi.visitors.ast.Nodes.TokenIdGenerator;

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
    List<FlowInput> getValues();
  }
  
  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    return Nodes.literal(ctx, token(ctx));
  }

  @Override
  public AstNode visitDataType(DataTypeContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDataType(ctx);
  }

  @Override
  public FlowBody visitFlow(FlowContext ctx) {
    Nodes children = nodes(ctx);
    return ImmutableFlowBody.builder()
        .token(token(ctx))
        .id(children.of(FwRedundentId.class).get().getValue())
        .description(children.of(FwRedundentDescription.class).map(e -> e.getValue()).orElse(null))
        .build();
  }

  @Override
  public FlowInputs visitInputs(InputsContext ctx) {
    List<FlowInput> values = nodes(ctx).of(FwRedundentInputArgs.class)
        .map(a -> a.getValues()).orElse(Collections.emptyList());
    return ImmutableFlowInputs.builder()
        .token(token(ctx))
        .values(values)
        .build();
  }

  @Override
  public AstNode visitInputArgs(InputArgsContext ctx) {
    return ImmutableFwRedundentInputArgs.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(FlowInput.class))
        .build();
  }

  @Override
  public AstNode visitInput(InputContext ctx) {
    // TODO Auto-generated method stub
    return ImmutableFlowIn;
  }

  
  @Override
  public AstNode visitTasks(TasksContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTasks(ctx);
  }

  @Override
  public AstNode visitTaskArgs(TaskArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTaskArgs(ctx);
  }

  @Override
  public AstNode visitTaskBody(TaskBodyContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTaskBody(ctx);
  }

  @Override
  public AstNode visitConditionalThen(ConditionalThenContext ctx) {
    // TODO Auto-generated method stub
    return super.visitConditionalThen(ctx);
  }

  @Override
  public AstNode visitWhenThenArgs(WhenThenArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitWhenThenArgs(ctx);
  }

  @Override
  public AstNode visitWhenThen(WhenThenContext ctx) {
    // TODO Auto-generated method stub
    return super.visitWhenThen(ctx);
  }

  @Override
  public AstNode visitThen(ThenContext ctx) {
    // TODO Auto-generated method stub
    return super.visitThen(ctx);
  }

  @Override
  public AstNode visitTaskRef(TaskRefContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTaskRef(ctx);
  }

  @Override
  public AstNode visitMapping(MappingContext ctx) {
    // TODO Auto-generated method stub
    return super.visitMapping(ctx);
  }

  @Override
  public AstNode visitMappingArgs(MappingArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitMappingArgs(ctx);
  }

  @Override
  public AstNode visitDebugValue(DebugValueContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDebugValue(ctx);
  }

  @Override
  public AstNode visitTask(TaskContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTask(ctx);
  }

  @Override
  public AstNode visitMappingArg(MappingArgContext ctx) {
    // TODO Auto-generated method stub
    return super.visitMappingArg(ctx);
  }

  @Override
  public AstNode visitMappingValue(MappingValueContext ctx) {
    // TODO Auto-generated method stub
    return super.visitMappingValue(ctx);
  }

  @Override
  public AstNode visitEndMapping(EndMappingContext ctx) {
    // TODO Auto-generated method stub
    return super.visitEndMapping(ctx);
  }
  
  @Override
  public FwRedundentId visitId(IdContext ctx) {
    return ImmutableFwRedundentId.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(DtRedundentTypeName.class).get().getValue())
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

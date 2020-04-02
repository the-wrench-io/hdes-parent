package io.resys.hdes.ast.spi.visitors.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import io.resys.hdes.ast.FlowParser.ConditionalThenContext;
import io.resys.hdes.ast.FlowParser.DataTypeContext;
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
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.spi.visitors.ast.Nodes.TokenIdGenerator;

public class FlParserAstNodeVisitor extends FlowParserBaseVisitor<AstNode> {
  private final TokenIdGenerator tokenIdGenerator;

  public FlParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
  }
  
  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    // TODO Auto-generated method stub
    return super.visitLiteral(ctx);
  }

  @Override
  public AstNode visitDataType(DataTypeContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDataType(ctx);
  }

  @Override
  public AstNode visitFlow(FlowContext ctx) {
    // TODO Auto-generated method stub
    return super.visitFlow(ctx);
  }

  @Override
  public AstNode visitInputs(InputsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitInputs(ctx);
  }

  @Override
  public AstNode visitInputArgs(InputArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitInputArgs(ctx);
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
  public AstNode visitTypeName(TypeNameContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTypeName(ctx);
  }

  @Override
  public AstNode visitId(IdContext ctx) {
    // TODO Auto-generated method stub
    return super.visitId(ctx);
  }

  @Override
  public AstNode visitDescription(DescriptionContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDescription(ctx);
  }

  @Override
  public AstNode visitInput(InputContext ctx) {
    // TODO Auto-generated method stub
    return super.visitInput(ctx);
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

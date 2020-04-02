package io.resys.hdes.ast.spi.visitors;

import static io.resys.hdes.ast.spi.visitors.ParserContextLogger.log;

import io.resys.hdes.ast.FlowParser;
import io.resys.hdes.ast.FlowParser.ConditionalThenContext;
import io.resys.hdes.ast.FlowParser.DebugValueContext;
import io.resys.hdes.ast.FlowParser.InputContext;
import io.resys.hdes.ast.FlowParser.LiteralContext;
import io.resys.hdes.ast.FlowParser.MappingArgContext;
import io.resys.hdes.ast.FlowParser.MappingContext;
import io.resys.hdes.ast.FlowParser.MappingValueContext;
import io.resys.hdes.ast.FlowParser.TaskBodyContext;
import io.resys.hdes.ast.FlowParser.TaskContext;
import io.resys.hdes.ast.FlowParser.TaskRefContext;
import io.resys.hdes.ast.FlowParser.ThenContext;
import io.resys.hdes.ast.FlowParser.TypeNameContext;
import io.resys.hdes.ast.FlowParser.WhenThenContext;
import io.resys.hdes.ast.FlowParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;


public class FlowParserConsoleVisitor extends FlowParserBaseVisitor<AstNode> {
  @Override
  public AstNode visitConditionalThen(ConditionalThenContext ctx) {
    log(ctx);
    return super.visitConditionalThen(ctx);
  }

  @Override
  public AstNode visitWhenThen(WhenThenContext ctx) {
    log(ctx);
    return super.visitWhenThen(ctx);
  }

  @Override
  public AstNode visitMappingArg(MappingArgContext ctx) {
    log(ctx);
    return super.visitMappingArg(ctx);
  }

  @Override
  public AstNode visitMappingValue(MappingValueContext ctx) {
    log(ctx);
    return super.visitMappingValue(ctx);
  }

  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    log(ctx);
    return super.visitLiteral(ctx);
  }

  @Override
  public AstNode visitTypeName(TypeNameContext ctx) {
    log(ctx);
    return super.visitTypeName(ctx);
  }

  @Override
  public AstNode visitTask(TaskContext ctx) {
    log(ctx);
    return super.visitTask(ctx);
  }

  @Override
  public AstNode visitTaskBody(TaskBodyContext ctx) {
    log(ctx);
    return super.visitTaskBody(ctx);
  }

  @Override
  public AstNode visitThen(ThenContext ctx) {
    log(ctx);
    return super.visitThen(ctx);
  }

  @Override
  public AstNode visitTaskRef(TaskRefContext ctx) {
    log(ctx);
    return super.visitTaskRef(ctx);
  }

  @Override
  public AstNode visitMapping(MappingContext ctx) {
    log(ctx);
    return super.visitMapping(ctx);
  }

  @Override
  public AstNode visitDebugValue(DebugValueContext ctx) {
    log(ctx);
    return super.visitDebugValue(ctx);
  }

  @Override
  public AstNode visitId(FlowParser.IdContext ctx) {
    log(ctx);
    return visitChildren(ctx);
  }

  @Override
  public AstNode visitDescription(FlowParser.DescriptionContext ctx) {
    log(ctx);
    return visitChildren(ctx);
  }

  @Override
  public AstNode visitInputs(FlowParser.InputsContext ctx) {
    log(ctx);
    return visitChildren(ctx);
  }

  @Override
  public AstNode visitInput(InputContext ctx) {
    log(ctx);
    return visitChildren(ctx);
  }

  @Override
  public AstNode visitTasks(FlowParser.TasksContext ctx) {
    log(ctx);
    return visitChildren(ctx);
  }

  @Override
  public AstNode visitFlow(FlowParser.FlowContext ctx) {
    log(ctx);
    return visitChildren(ctx);
  }
}

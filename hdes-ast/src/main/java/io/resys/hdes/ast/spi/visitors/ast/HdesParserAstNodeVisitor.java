package io.resys.hdes.ast.spi.visitors.ast;

import io.resys.hdes.ast.HdesParser.BodyArgsContext;
import io.resys.hdes.ast.HdesParser.BodyContext;
import io.resys.hdes.ast.HdesParser.CompilationUnitContext;
import io.resys.hdes.ast.HdesParser.DefContext;
import io.resys.hdes.ast.HdesParser.DefDecisionTableContext;
import io.resys.hdes.ast.HdesParser.DefFlowContext;
import io.resys.hdes.ast.HdesParser.DefManualTaskContext;
import io.resys.hdes.ast.HdesParser.DefServiceTaskContext;
import io.resys.hdes.ast.HdesParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;

public class HdesParserAstNodeVisitor extends HdesParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitCompilationUnit(CompilationUnitContext ctx) {
    // TODO Auto-generated method stub
    return super.visitCompilationUnit(ctx);
  }

  @Override
  public AstNode visitDef(DefContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDef(ctx);
  }

  @Override
  public AstNode visitDefFlow(DefFlowContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDefFlow(ctx);
  }

  @Override
  public AstNode visitDefDecisionTable(DefDecisionTableContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDefDecisionTable(ctx);
  }

  @Override
  public AstNode visitDefManualTask(DefManualTaskContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDefManualTask(ctx);
  }

  @Override
  public AstNode visitDefServiceTask(DefServiceTaskContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDefServiceTask(ctx);
  }

  @Override
  public AstNode visitBodyArgs(BodyArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitBodyArgs(ctx);
  }

  @Override
  public AstNode visitBody(BodyContext ctx) {
    // TODO Auto-generated method stub
    return super.visitBody(ctx);
  }
}

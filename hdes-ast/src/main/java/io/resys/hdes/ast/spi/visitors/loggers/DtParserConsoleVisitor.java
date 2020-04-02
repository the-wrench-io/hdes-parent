package io.resys.hdes.ast.spi.visitors.loggers;

import static io.resys.hdes.ast.spi.visitors.loggers.ParserContextLogger.log;

import io.resys.hdes.ast.DecisionTableParser.AllContext;
import io.resys.hdes.ast.DecisionTableParser.DescriptionContext;
import io.resys.hdes.ast.DecisionTableParser.DtContext;
import io.resys.hdes.ast.DecisionTableParser.FirstContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderArgsContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderTypeContext;
import io.resys.hdes.ast.DecisionTableParser.HeadersContext;
import io.resys.hdes.ast.DecisionTableParser.HitPolicyContext;
import io.resys.hdes.ast.DecisionTableParser.IdContext;
import io.resys.hdes.ast.DecisionTableParser.LiteralContext;
import io.resys.hdes.ast.DecisionTableParser.MatrixContext;
import io.resys.hdes.ast.DecisionTableParser.RulesContext;
import io.resys.hdes.ast.DecisionTableParser.RulesetContext;
import io.resys.hdes.ast.DecisionTableParser.RulesetsContext;
import io.resys.hdes.ast.DecisionTableParser.TypeNameContext;
import io.resys.hdes.ast.DecisionTableParser.UndefinedValueContext;
import io.resys.hdes.ast.DecisionTableParser.ValueContext;
import io.resys.hdes.ast.DecisionTableParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;

public class DtParserConsoleVisitor extends DecisionTableParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitUndefinedValue(UndefinedValueContext ctx) {
    log(ctx);
    return super.visitUndefinedValue(ctx);
  }

  @Override
  public AstNode visitHeaderType(HeaderTypeContext ctx) {
    log(ctx);
    return super.visitHeaderType(ctx);
  }

  @Override
  public AstNode visitFirst(FirstContext ctx) {
    log(ctx);
    return super.visitFirst(ctx);
  }

  @Override
  public AstNode visitAll(AllContext ctx) {
    log(ctx);
    return super.visitAll(ctx);
  }

  @Override
  public AstNode visitMatrix(MatrixContext ctx) {
    log(ctx);
    return super.visitMatrix(ctx);
  }

  @Override
  public AstNode visitRulesets(RulesetsContext ctx) {
    log(ctx);
    return super.visitRulesets(ctx);
  }

  @Override
  public AstNode visitRuleset(RulesetContext ctx) {
    log(ctx);
    return super.visitRuleset(ctx);
  }

  @Override
  public AstNode visitRules(RulesContext ctx) {
    log(ctx);
    return super.visitRules(ctx);
  }

  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    log(ctx);
    return super.visitLiteral(ctx);
  }

  @Override
  public AstNode visitDt(DtContext ctx) {
    log(ctx);
    return super.visitDt(ctx);
  }

  @Override
  public AstNode visitHeaders(HeadersContext ctx) {
    log(ctx);
    return super.visitHeaders(ctx);
  }

  @Override
  public AstNode visitHeaderArgs(HeaderArgsContext ctx) {
    log(ctx);
    return super.visitHeaderArgs(ctx);
  }

  @Override
  public AstNode visitHeader(HeaderContext ctx) {
    log(ctx);
    return super.visitHeader(ctx);
  }

  @Override
  public AstNode visitValue(ValueContext ctx) {
    log(ctx);
    return super.visitValue(ctx);
  }

  @Override
  public AstNode visitId(IdContext ctx) {
    log(ctx);
    return super.visitId(ctx);
  }

  @Override
  public AstNode visitDescription(DescriptionContext ctx) {
    log(ctx);
    return super.visitDescription(ctx);
  }

  @Override
  public AstNode visitTypeName(TypeNameContext ctx) {
    log(ctx);
    return super.visitTypeName(ctx);
  }

  @Override
  public AstNode visitHitPolicy(HitPolicyContext ctx) {
    log(ctx);
    return super.visitHitPolicy(ctx);
  }
}

package io.resys.hdes.ast.spi.visitors.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import io.resys.hdes.ast.ManualTaskParser.CssClassArgsContext;
import io.resys.hdes.ast.ManualTaskParser.CssClassContext;
import io.resys.hdes.ast.ManualTaskParser.DataTypeContext;
import io.resys.hdes.ast.ManualTaskParser.DefaultValueContext;
import io.resys.hdes.ast.ManualTaskParser.DescriptionContext;
import io.resys.hdes.ast.ManualTaskParser.FieldArgsContext;
import io.resys.hdes.ast.ManualTaskParser.FieldContext;
import io.resys.hdes.ast.ManualTaskParser.FieldsContext;
import io.resys.hdes.ast.ManualTaskParser.FormContext;
import io.resys.hdes.ast.ManualTaskParser.GroupArgsContext;
import io.resys.hdes.ast.ManualTaskParser.GroupContext;
import io.resys.hdes.ast.ManualTaskParser.GroupsContext;
import io.resys.hdes.ast.ManualTaskParser.IdContext;
import io.resys.hdes.ast.ManualTaskParser.InputArgsContext;
import io.resys.hdes.ast.ManualTaskParser.InputContext;
import io.resys.hdes.ast.ManualTaskParser.InputsContext;
import io.resys.hdes.ast.ManualTaskParser.LiteralContext;
import io.resys.hdes.ast.ManualTaskParser.MessageContext;
import io.resys.hdes.ast.ManualTaskParser.MtContext;
import io.resys.hdes.ast.ManualTaskParser.PropsContext;
import io.resys.hdes.ast.ManualTaskParser.StatementContext;
import io.resys.hdes.ast.ManualTaskParser.StatementsArgsContext;
import io.resys.hdes.ast.ManualTaskParser.StatementsContext;
import io.resys.hdes.ast.ManualTaskParser.ThenContext;
import io.resys.hdes.ast.ManualTaskParser.TypeNameContext;
import io.resys.hdes.ast.ManualTaskParser.WhenContext;
import io.resys.hdes.ast.ManualTaskParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.spi.visitors.ast.Nodes.TokenIdGenerator;

public class MtParserAstNodeVisitor extends ManualTaskParserBaseVisitor<AstNode> {
  private final TokenIdGenerator tokenIdGenerator;

  public MtParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
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
  public AstNode visitMt(MtContext ctx) {
    // TODO Auto-generated method stub
    return super.visitMt(ctx);
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
  public AstNode visitForm(FormContext ctx) {
    // TODO Auto-generated method stub
    return super.visitForm(ctx);
  }


  @Override
  public AstNode visitGroups(GroupsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitGroups(ctx);
  }


  @Override
  public AstNode visitGroupArgs(GroupArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitGroupArgs(ctx);
  }


  @Override
  public AstNode visitGroup(GroupContext ctx) {
    // TODO Auto-generated method stub
    return super.visitGroup(ctx);
  }


  @Override
  public AstNode visitFields(FieldsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitFields(ctx);
  }


  @Override
  public AstNode visitFieldArgs(FieldArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitFieldArgs(ctx);
  }


  @Override
  public AstNode visitField(FieldContext ctx) {
    // TODO Auto-generated method stub
    return super.visitField(ctx);
  }


  @Override
  public AstNode visitProps(PropsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitProps(ctx);
  }


  @Override
  public AstNode visitCssClass(CssClassContext ctx) {
    // TODO Auto-generated method stub
    return super.visitCssClass(ctx);
  }


  @Override
  public AstNode visitCssClassArgs(CssClassArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitCssClassArgs(ctx);
  }


  @Override
  public AstNode visitStatements(StatementsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitStatements(ctx);
  }


  @Override
  public AstNode visitStatementsArgs(StatementsArgsContext ctx) {
    // TODO Auto-generated method stub
    return super.visitStatementsArgs(ctx);
  }


  @Override
  public AstNode visitStatement(StatementContext ctx) {
    // TODO Auto-generated method stub
    return super.visitStatement(ctx);
  }


  @Override
  public AstNode visitWhen(WhenContext ctx) {
    // TODO Auto-generated method stub
    return super.visitWhen(ctx);
  }


  @Override
  public AstNode visitThen(ThenContext ctx) {
    // TODO Auto-generated method stub
    return super.visitThen(ctx);
  }


  @Override
  public AstNode visitMessage(MessageContext ctx) {
    // TODO Auto-generated method stub
    return super.visitMessage(ctx);
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
  public AstNode visitTypeName(TypeNameContext ctx) {
    // TODO Auto-generated method stub
    return super.visitTypeName(ctx);
  }


  @Override
  public AstNode visitInput(InputContext ctx) {
    // TODO Auto-generated method stub
    return super.visitInput(ctx);
  }


  @Override
  public AstNode visitDefaultValue(DefaultValueContext ctx) {
    // TODO Auto-generated method stub
    return super.visitDefaultValue(ctx);
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

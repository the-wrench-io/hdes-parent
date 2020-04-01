package io.resys.hdes.ast.spi.flow.visitors;

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

import org.antlr.v4.runtime.ParserRuleContext;

import io.resys.hdes.ast.ManualTaskParser.CssClassArgsContext;
import io.resys.hdes.ast.ManualTaskParser.CssClassContext;
import io.resys.hdes.ast.ManualTaskParser.DataTypeContext;
import io.resys.hdes.ast.ManualTaskParser.DefaultValueContext;
import io.resys.hdes.ast.ManualTaskParser.DescriptionContext;
import io.resys.hdes.ast.ManualTaskParser.FieldContext;
import io.resys.hdes.ast.ManualTaskParser.FieldsContext;
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
import io.resys.hdes.ast.ManualTaskParser.StatementsContext;
import io.resys.hdes.ast.ManualTaskParser.ThenContext;
import io.resys.hdes.ast.ManualTaskParser.TypeNameContext;
import io.resys.hdes.ast.ManualTaskParser.WhenContext;
import io.resys.hdes.ast.ManualTaskParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;

public class ManualTaskParserConsoleVisitor extends ManualTaskParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    log(ctx);
    return super.visitLiteral(ctx);
  }

  @Override
  public AstNode visitDataType(DataTypeContext ctx) {
    log(ctx);
    return super.visitDataType(ctx);
  }

  @Override
  public AstNode visitMt(MtContext ctx) {
    log(ctx);
    return super.visitMt(ctx);
  }

  @Override
  public AstNode visitInputs(InputsContext ctx) {
    log(ctx);
    return super.visitInputs(ctx);
  }

  @Override
  public AstNode visitInputArgs(InputArgsContext ctx) {
    log(ctx);
    return super.visitInputArgs(ctx);
  }

  @Override
  public AstNode visitGroups(GroupsContext ctx) {
    log(ctx);
    return super.visitGroups(ctx);
  }

  @Override
  public AstNode visitFields(FieldsContext ctx) {
    log(ctx);
    return super.visitFields(ctx);
  }

  @Override
  public AstNode visitField(FieldContext ctx) {
    log(ctx);
    return super.visitField(ctx);
  }

  @Override
  public AstNode visitProps(PropsContext ctx) {
    log(ctx);
    return super.visitProps(ctx);
  }

  @Override
  public AstNode visitCssClass(CssClassContext ctx) {
    log(ctx);
    return super.visitCssClass(ctx);
  }

  @Override
  public AstNode visitCssClassArgs(CssClassArgsContext ctx) {
    log(ctx);
    return super.visitCssClassArgs(ctx);
  }

  @Override
  public AstNode visitStatements(StatementsContext ctx) {
    log(ctx);
    return super.visitStatements(ctx);
  }

  @Override
  public AstNode visitStatement(StatementContext ctx) {
    log(ctx);
    return super.visitStatement(ctx);
  }

  @Override
  public AstNode visitWhen(WhenContext ctx) {
    log(ctx);
    return super.visitWhen(ctx);
  }

  @Override
  public AstNode visitThen(ThenContext ctx) {
    log(ctx);
    return super.visitThen(ctx);
  }

  @Override
  public AstNode visitMessage(MessageContext ctx) {
    log(ctx);
    return super.visitMessage(ctx);
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
  public AstNode visitInput(InputContext ctx) {
    log(ctx);
    return super.visitInput(ctx);
  }

  @Override
  public AstNode visitDefaultValue(DefaultValueContext ctx) {
    log(ctx);
    return super.visitDefaultValue(ctx);
  }

  private static final void log(ParserRuleContext context) {
    System.out.println("visiting: " + context.getClass().getSimpleName() 
        //+ ": " + context.getText()
        );
  }

}

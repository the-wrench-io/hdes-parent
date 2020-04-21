package io.resys.hdes.ast.spi.visitors.loggers;

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

import static io.resys.hdes.ast.spi.visitors.loggers.ParserContextLogger.log;

import io.resys.hdes.ast.ManualTaskParser.CssClassContext;
import io.resys.hdes.ast.ManualTaskParser.DefaultValueContext;
import io.resys.hdes.ast.ManualTaskParser.DescriptionContext;
import io.resys.hdes.ast.ManualTaskParser.FieldContext;
import io.resys.hdes.ast.ManualTaskParser.FieldsContext;
import io.resys.hdes.ast.ManualTaskParser.GroupsContext;
import io.resys.hdes.ast.ManualTaskParser.IdContext;
import io.resys.hdes.ast.ManualTaskParser.InputsContext;
import io.resys.hdes.ast.ManualTaskParser.LiteralContext;
import io.resys.hdes.ast.ManualTaskParser.MessageContext;
import io.resys.hdes.ast.ManualTaskParser.MtContext;
import io.resys.hdes.ast.ManualTaskParser.TypeNameContext;
import io.resys.hdes.ast.ManualTaskParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;


public class ManualTaskParserConsoleVisitor extends ManualTaskParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    log(ctx);
    return super.visitLiteral(ctx);
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
  public AstNode visitCssClass(CssClassContext ctx) {
    log(ctx);
    return super.visitCssClass(ctx);
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
  public AstNode visitDefaultValue(DefaultValueContext ctx) {
    log(ctx);
    return super.visitDefaultValue(ctx);
  }
}

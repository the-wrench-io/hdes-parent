package io.resys.hdes.ast.spi.visitors;

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

import io.resys.hdes.ast.DecisionTableParser.DescriptionContext;
import io.resys.hdes.ast.DecisionTableParser.DtContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderArgsContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderContext;
import io.resys.hdes.ast.DecisionTableParser.HeadersContext;
import io.resys.hdes.ast.DecisionTableParser.HitPolicyContext;
import io.resys.hdes.ast.DecisionTableParser.IdContext;
import io.resys.hdes.ast.DecisionTableParser.LiteralContext;
import io.resys.hdes.ast.DecisionTableParser.TypeNameContext;
import io.resys.hdes.ast.DecisionTableParser.ValueContext;
import io.resys.hdes.ast.DecisionTableParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;

public class DtParserConsoleVisitor extends DecisionTableParserBaseVisitor<AstNode> {

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

  private static final void log(ParserRuleContext context) {
    System.out.println("visiting: " + context.getClass().getSimpleName() 
        //+ ": " + context.getText()
        );
  }

}

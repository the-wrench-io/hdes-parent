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
import io.resys.hdes.ast.api.AstNode;

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

  private static final void log(ParserRuleContext context) {
    System.out.println("visiting: " + context.getClass().getSimpleName() 
        //+ ": " + context.getText()
        );
  }

}

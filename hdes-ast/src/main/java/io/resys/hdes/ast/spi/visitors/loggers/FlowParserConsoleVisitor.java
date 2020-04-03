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

import io.resys.hdes.ast.FlowParser;
import io.resys.hdes.ast.FlowParser.ArrayTypeContext;
import io.resys.hdes.ast.FlowParser.DebugValueContext;
import io.resys.hdes.ast.FlowParser.EndTaskContext;
import io.resys.hdes.ast.FlowParser.InputArgsContext;
import io.resys.hdes.ast.FlowParser.InputContext;
import io.resys.hdes.ast.FlowParser.LiteralContext;
import io.resys.hdes.ast.FlowParser.MappingArgContext;
import io.resys.hdes.ast.FlowParser.MappingArgsContext;
import io.resys.hdes.ast.FlowParser.MappingContext;
import io.resys.hdes.ast.FlowParser.MappingValueContext;
import io.resys.hdes.ast.FlowParser.NextTaskContext;
import io.resys.hdes.ast.FlowParser.ObjectTypeContext;
import io.resys.hdes.ast.FlowParser.PointerContext;
import io.resys.hdes.ast.FlowParser.ScalarTypeContext;
import io.resys.hdes.ast.FlowParser.SimpleTypeContext;
import io.resys.hdes.ast.FlowParser.TaskArgsContext;
import io.resys.hdes.ast.FlowParser.TaskContext;
import io.resys.hdes.ast.FlowParser.TaskRefContext;
import io.resys.hdes.ast.FlowParser.TaskTypesContext;
import io.resys.hdes.ast.FlowParser.ThenContext;
import io.resys.hdes.ast.FlowParser.TypeNameContext;
import io.resys.hdes.ast.FlowParser.WhenExpressionContext;
import io.resys.hdes.ast.FlowParser.WhenThenArgsContext;
import io.resys.hdes.ast.FlowParser.WhenThenContext;
import io.resys.hdes.ast.FlowParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;


public class FlowParserConsoleVisitor extends FlowParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitTaskTypes(TaskTypesContext ctx) {
    log(ctx);
    return super.visitTaskTypes(ctx);
  }

  @Override
  public AstNode visitScalarType(ScalarTypeContext ctx) {
    log(ctx);
    return super.visitScalarType(ctx);
  }

  @Override
  public AstNode visitInputArgs(InputArgsContext ctx) {
    log(ctx);
    return super.visitInputArgs(ctx);
  }

  @Override
  public AstNode visitTaskArgs(TaskArgsContext ctx) {
    log(ctx);
    return super.visitTaskArgs(ctx);
  }

  @Override
  public AstNode visitPointer(PointerContext ctx) {
    log(ctx);
    return super.visitPointer(ctx);
  }

  @Override
  public AstNode visitWhenThenArgs(WhenThenArgsContext ctx) {
    log(ctx);
    return super.visitWhenThenArgs(ctx);
  }

  @Override
  public AstNode visitWhenExpression(WhenExpressionContext ctx) {
    log(ctx);
    return super.visitWhenExpression(ctx);
  }

  @Override
  public AstNode visitMappingArgs(MappingArgsContext ctx) {
    log(ctx);
    return super.visitMappingArgs(ctx);
  }

  @Override
  public AstNode visitSimpleType(SimpleTypeContext ctx) {
    log(ctx);
    return super.visitSimpleType(ctx);
  }

  @Override
  public AstNode visitArrayType(ArrayTypeContext ctx) {
    log(ctx);
    return super.visitArrayType(ctx);
  }

  @Override
  public AstNode visitObjectType(ObjectTypeContext ctx) {
    log(ctx);
    return super.visitObjectType(ctx);
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
  public AstNode visitEndTask(EndTaskContext ctx) {
    log(ctx);
    return super.visitEndTask(ctx);
  }

  @Override
  public AstNode visitNextTask(NextTaskContext ctx) {
    log(ctx);
    return super.visitNextTask(ctx);
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

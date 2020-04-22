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

import io.resys.hdes.ast.HdesParser.ActionBodyThenContext;
import io.resys.hdes.ast.HdesParser.ActionBodyWhenContext;
import io.resys.hdes.ast.HdesParser.ActionContext;
import io.resys.hdes.ast.HdesParser.ActionTypeContext;
import io.resys.hdes.ast.HdesParser.ActionsArgsContext;
import io.resys.hdes.ast.HdesParser.ActionsContext;
import io.resys.hdes.ast.HdesParser.AdditiveExpressionContext;
import io.resys.hdes.ast.HdesParser.AllContext;
import io.resys.hdes.ast.HdesParser.AndExpressionContext;
import io.resys.hdes.ast.HdesParser.ArrayTypeContext;
import io.resys.hdes.ast.HdesParser.ConditionalAndExpressionContext;
import io.resys.hdes.ast.HdesParser.ConditionalExpressionContext;
import io.resys.hdes.ast.HdesParser.ConditionalOrExpressionContext;
import io.resys.hdes.ast.HdesParser.CssClassContext;
import io.resys.hdes.ast.HdesParser.DebugValueContext;
import io.resys.hdes.ast.HdesParser.DefaultValueContext;
import io.resys.hdes.ast.HdesParser.DescriptionContext;
import io.resys.hdes.ast.HdesParser.DirectionTypeContext;
import io.resys.hdes.ast.HdesParser.DropdownArgContext;
import io.resys.hdes.ast.HdesParser.DropdownArgsContext;
import io.resys.hdes.ast.HdesParser.DropdownContext;
import io.resys.hdes.ast.HdesParser.DropdownKeyAndValueContext;
import io.resys.hdes.ast.HdesParser.DropdownKeysAndValuesContext;
import io.resys.hdes.ast.HdesParser.DropdownTypeContext;
import io.resys.hdes.ast.HdesParser.DropdownsContext;
import io.resys.hdes.ast.HdesParser.DtBodyContext;
import io.resys.hdes.ast.HdesParser.EnBodyContext;
import io.resys.hdes.ast.HdesParser.EndMappingContext;
import io.resys.hdes.ast.HdesParser.EqualityExpressionContext;
import io.resys.hdes.ast.HdesParser.ExpressionContext;
import io.resys.hdes.ast.HdesParser.FieldArgsContext;
import io.resys.hdes.ast.HdesParser.FieldContext;
import io.resys.hdes.ast.HdesParser.FieldsContext;
import io.resys.hdes.ast.HdesParser.FirstContext;
import io.resys.hdes.ast.HdesParser.FlBodyContext;
import io.resys.hdes.ast.HdesParser.FormContext;
import io.resys.hdes.ast.HdesParser.GroupArgsContext;
import io.resys.hdes.ast.HdesParser.GroupContext;
import io.resys.hdes.ast.HdesParser.GroupsContext;
import io.resys.hdes.ast.HdesParser.HdesBodyContext;
import io.resys.hdes.ast.HdesParser.HeadersContext;
import io.resys.hdes.ast.HdesParser.HitPolicyContext;
import io.resys.hdes.ast.HdesParser.IdContext;
import io.resys.hdes.ast.HdesParser.LiteralContext;
import io.resys.hdes.ast.HdesParser.MappingArgContext;
import io.resys.hdes.ast.HdesParser.MappingArgsContext;
import io.resys.hdes.ast.HdesParser.MappingContext;
import io.resys.hdes.ast.HdesParser.MappingValueContext;
import io.resys.hdes.ast.HdesParser.MatrixContext;
import io.resys.hdes.ast.HdesParser.MessageContext;
import io.resys.hdes.ast.HdesParser.MethodArgsContext;
import io.resys.hdes.ast.HdesParser.MethodInvocationContext;
import io.resys.hdes.ast.HdesParser.MethodNameContext;
import io.resys.hdes.ast.HdesParser.MtBodyContext;
import io.resys.hdes.ast.HdesParser.MultiplicativeExpressionContext;
import io.resys.hdes.ast.HdesParser.NextTaskContext;
import io.resys.hdes.ast.HdesParser.ObjectDataTypeContext;
import io.resys.hdes.ast.HdesParser.ObjectTypeContext;
import io.resys.hdes.ast.HdesParser.PostfixExpressionContext;
import io.resys.hdes.ast.HdesParser.PreDecrementExpressionContext;
import io.resys.hdes.ast.HdesParser.PreIncrementExpressionContext;
import io.resys.hdes.ast.HdesParser.PrimaryContext;
import io.resys.hdes.ast.HdesParser.RelationalExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleEqualityExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleMatchingExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleMatchingOrExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleRelationalExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleUndefinedValueContext;
import io.resys.hdes.ast.HdesParser.RuleValueContext;
import io.resys.hdes.ast.HdesParser.RulesContext;
import io.resys.hdes.ast.HdesParser.RulesetContext;
import io.resys.hdes.ast.HdesParser.RulesetsContext;
import io.resys.hdes.ast.HdesParser.ScalarTypeContext;
import io.resys.hdes.ast.HdesParser.SimpleTypeContext;
import io.resys.hdes.ast.HdesParser.TaskArgsContext;
import io.resys.hdes.ast.HdesParser.TaskRefContext;
import io.resys.hdes.ast.HdesParser.TaskTypesContext;
import io.resys.hdes.ast.HdesParser.TasksContext;
import io.resys.hdes.ast.HdesParser.TypeDefArgsContext;
import io.resys.hdes.ast.HdesParser.TypeDefContext;
import io.resys.hdes.ast.HdesParser.TypeDefsContext;
import io.resys.hdes.ast.HdesParser.TypeNameContext;
import io.resys.hdes.ast.HdesParser.UnaryExpressionContext;
import io.resys.hdes.ast.HdesParser.UnaryExpressionNotPlusMinusContext;
import io.resys.hdes.ast.HdesParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;

public class HdesParserConsoleVisitor extends HdesParserBaseVisitor<AstNode> {

  @Override
  public AstNode visitRuleValue(RuleValueContext ctx) {
    log(ctx);
    return super.visitRuleValue(ctx);
  }

  @Override
  public AstNode visitRuleMatchingExpression(RuleMatchingExpressionContext ctx) {
    log(ctx);
    return super.visitRuleMatchingExpression(ctx);
  }

  @Override
  public AstNode visitRuleMatchingOrExpression(RuleMatchingOrExpressionContext ctx) {
    log(ctx);
    return super.visitRuleMatchingOrExpression(ctx);
  }

  @Override
  public AstNode visitRuleEqualityExpression(RuleEqualityExpressionContext ctx) {
    log(ctx);
    return super.visitRuleEqualityExpression(ctx);
  }

  @Override
  public AstNode visitRuleRelationalExpression(RuleRelationalExpressionContext ctx) {
    log(ctx);
    return super.visitRuleRelationalExpression(ctx);
  }

  @Override
  public AstNode visitRuleUndefinedValue(RuleUndefinedValueContext ctx) {
    log(ctx);
    return super.visitRuleUndefinedValue(ctx);
  }

  @Override
  public AstNode visitScalarType(ScalarTypeContext ctx) {
    log(ctx);
    return super.visitScalarType(ctx);
  }

  @Override
  public AstNode visitFirst(FirstContext ctx) {
    log(ctx);
    return super.visitFirst(ctx);
  }

  @Override
  public AstNode visitDirectionType(DirectionTypeContext ctx) {
    log(ctx);
    return super.visitDirectionType(ctx);
  }

  @Override
  public AstNode visitTypeDefs(TypeDefsContext ctx) {
    log(ctx);
    return super.visitTypeDefs(ctx);
  }

  @Override
  public AstNode visitHdesBody(HdesBodyContext ctx) {
    log(ctx);
    return super.visitHdesBody(ctx);
  }

  @Override
  public AstNode visitDtBody(DtBodyContext ctx) {
    log(ctx);
    return super.visitDtBody(ctx);
  }

  @Override
  public AstNode visitMethodName(MethodNameContext ctx) {
    log(ctx);
    return super.visitMethodName(ctx);
  }

  @Override
  public AstNode visitMethodInvocation(MethodInvocationContext ctx) {
    log(ctx);
    return super.visitMethodInvocation(ctx);
  }

  @Override
  public AstNode visitMethodArgs(MethodArgsContext ctx) {
    log(ctx);
    return super.visitMethodArgs(ctx);
  }

  @Override
  public AstNode visitPrimary(PrimaryContext ctx) {
    log(ctx);
    return super.visitPrimary(ctx);
  }

  @Override
  public AstNode visitEnBody(EnBodyContext ctx) {
    log(ctx);
    return super.visitEnBody(ctx);
  }

  @Override
  public AstNode visitExpression(ExpressionContext ctx) {
    log(ctx);
    return super.visitExpression(ctx);
  }

  @Override
  public AstNode visitConditionalExpression(ConditionalExpressionContext ctx) {
    log(ctx);
    return super.visitConditionalExpression(ctx);
  }

  @Override
  public AstNode visitConditionalOrExpression(ConditionalOrExpressionContext ctx) {
    log(ctx);
    return super.visitConditionalOrExpression(ctx);
  }

  @Override
  public AstNode visitConditionalAndExpression(ConditionalAndExpressionContext ctx) {
    log(ctx);
    return super.visitConditionalAndExpression(ctx);
  }

  @Override
  public AstNode visitAndExpression(AndExpressionContext ctx) {
    log(ctx);
    return super.visitAndExpression(ctx);
  }

  @Override
  public AstNode visitEqualityExpression(EqualityExpressionContext ctx) {
    log(ctx);
    return super.visitEqualityExpression(ctx);
  }

  @Override
  public AstNode visitRelationalExpression(RelationalExpressionContext ctx) {
    log(ctx);
    return super.visitRelationalExpression(ctx);
  }

  @Override
  public AstNode visitAdditiveExpression(AdditiveExpressionContext ctx) {
    log(ctx);
    return super.visitAdditiveExpression(ctx);
  }

  @Override
  public AstNode visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
    log(ctx);
    return super.visitMultiplicativeExpression(ctx);
  }

  @Override
  public AstNode visitUnaryExpression(UnaryExpressionContext ctx) {
    log(ctx);
    return super.visitUnaryExpression(ctx);
  }

  @Override
  public AstNode visitPreIncrementExpression(PreIncrementExpressionContext ctx) {
    log(ctx);
    return super.visitPreIncrementExpression(ctx);
  }

  @Override
  public AstNode visitPreDecrementExpression(PreDecrementExpressionContext ctx) {
    log(ctx);
    return super.visitPreDecrementExpression(ctx);
  }

  @Override
  public AstNode visitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {
    log(ctx);
    return super.visitUnaryExpressionNotPlusMinus(ctx);
  }

  @Override
  public AstNode visitPostfixExpression(PostfixExpressionContext ctx) {
    log(ctx);
    return super.visitPostfixExpression(ctx);
  }

  @Override
  public AstNode visitTaskTypes(TaskTypesContext ctx) {
    log(ctx);
    return super.visitTaskTypes(ctx);
  }

  @Override
  public AstNode visitObjectDataType(ObjectDataTypeContext ctx) {
    log(ctx);
    return super.visitObjectDataType(ctx);
  }

  @Override
  public AstNode visitFlBody(FlBodyContext ctx) {
    log(ctx);
    return super.visitFlBody(ctx);
  }

  @Override
  public AstNode visitTasks(TasksContext ctx) {
    log(ctx);
    return super.visitTasks(ctx);
  }

  @Override
  public AstNode visitTaskArgs(TaskArgsContext ctx) {
    log(ctx);
    return super.visitTaskArgs(ctx);
  }

  @Override
  public AstNode visitNextTask(NextTaskContext ctx) {
    log(ctx);
    return super.visitNextTask(ctx);
  }

  @Override
  public AstNode visitEndMapping(EndMappingContext ctx) {
    log(ctx);
    return super.visitEndMapping(ctx);
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
  public AstNode visitMappingArgs(MappingArgsContext ctx) {
    log(ctx);
    return super.visitMappingArgs(ctx);
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
  public AstNode visitDropdownType(DropdownTypeContext ctx) {
    log(ctx);
    return super.visitDropdownType(ctx);
  }

  @Override
  public AstNode visitMtBody(MtBodyContext ctx) {
    log(ctx);
    return super.visitMtBody(ctx);
  }

  @Override
  public AstNode visitDropdowns(DropdownsContext ctx) {
    log(ctx);
    return super.visitDropdowns(ctx);
  }

  @Override
  public AstNode visitDropdownArgs(DropdownArgsContext ctx) {
    log(ctx);
    return super.visitDropdownArgs(ctx);
  }

  @Override
  public AstNode visitDropdownArg(DropdownArgContext ctx) {
    log(ctx);
    return super.visitDropdownArg(ctx);
  }

  @Override
  public AstNode visitDropdownKeysAndValues(DropdownKeysAndValuesContext ctx) {
    log(ctx);
    return super.visitDropdownKeysAndValues(ctx);
  }

  @Override
  public AstNode visitDropdownKeyAndValue(DropdownKeyAndValueContext ctx) {
    log(ctx);
    return super.visitDropdownKeyAndValue(ctx);
  }

  @Override
  public AstNode visitForm(FormContext ctx) {
    log(ctx);
    return super.visitForm(ctx);
  }

  @Override
  public AstNode visitGroups(GroupsContext ctx) {
    log(ctx);
    return super.visitGroups(ctx);
  }

  @Override
  public AstNode visitGroupArgs(GroupArgsContext ctx) {
    log(ctx);
    return super.visitGroupArgs(ctx);
  }

  @Override
  public AstNode visitGroup(GroupContext ctx) {
    log(ctx);
    return super.visitGroup(ctx);
  }

  @Override
  public AstNode visitFields(FieldsContext ctx) {
    log(ctx);
    return super.visitFields(ctx);
  }

  @Override
  public AstNode visitFieldArgs(FieldArgsContext ctx) {
    log(ctx);
    return super.visitFieldArgs(ctx);
  }

  @Override
  public AstNode visitField(FieldContext ctx) {
    log(ctx);
    return super.visitField(ctx);
  }

  @Override
  public AstNode visitDropdown(DropdownContext ctx) {
    log(ctx);
    return super.visitDropdown(ctx);
  }

  @Override
  public AstNode visitDefaultValue(DefaultValueContext ctx) {
    log(ctx);
    return super.visitDefaultValue(ctx);
  }

  @Override
  public AstNode visitCssClass(CssClassContext ctx) {
    log(ctx);
    return super.visitCssClass(ctx);
  }

  @Override
  public AstNode visitActions(ActionsContext ctx) {
    log(ctx);
    return super.visitActions(ctx);
  }

  @Override
  public AstNode visitActionsArgs(ActionsArgsContext ctx) {
    log(ctx);
    return super.visitActionsArgs(ctx);
  }

  @Override
  public AstNode visitAction(ActionContext ctx) {
    log(ctx);
    return super.visitAction(ctx);
  }

  @Override
  public AstNode visitActionBodyWhen(ActionBodyWhenContext ctx) {
    log(ctx);
    return super.visitActionBodyWhen(ctx);
  }

  @Override
  public AstNode visitActionBodyThen(ActionBodyThenContext ctx) {
    log(ctx);
    return super.visitActionBodyThen(ctx);
  }

  @Override
  public AstNode visitActionType(ActionTypeContext ctx) {
    log(ctx);
    return super.visitActionType(ctx);
  }

  @Override
  public AstNode visitMessage(MessageContext ctx) {
    log(ctx);
    return super.visitMessage(ctx);
  }

  @Override
  public AstNode visitTypeDefArgs(TypeDefArgsContext ctx) {
    log(ctx);
    return super.visitTypeDefArgs(ctx);
  }

  @Override
  public AstNode visitTypeDef(TypeDefContext ctx) {
    log(ctx);
    return super.visitTypeDef(ctx);
  }

  @Override
  public AstNode visitSimpleType(SimpleTypeContext ctx) {
    log(ctx);
    return super.visitSimpleType(ctx);
  }

  @Override
  public AstNode visitObjectType(ObjectTypeContext ctx) {
    log(ctx);
    return super.visitObjectType(ctx);
  }

  @Override
  public AstNode visitArrayType(ArrayTypeContext ctx) {
    log(ctx);
    return super.visitArrayType(ctx);
  }

  @Override
  public AstNode visitDebugValue(DebugValueContext ctx) {
    log(ctx);
    return super.visitDebugValue(ctx);
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
  public AstNode visitHeaders(HeadersContext ctx) {
    log(ctx);
    return super.visitHeaders(ctx);
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

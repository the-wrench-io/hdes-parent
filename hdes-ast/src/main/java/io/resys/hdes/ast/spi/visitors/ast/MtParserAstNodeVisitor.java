package io.resys.hdes.ast.spi.visitors.ast;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.FlowParser;
import io.resys.hdes.ast.ManualTaskParser;
import io.resys.hdes.ast.ManualTaskParser.ArrayTypeContext;
import io.resys.hdes.ast.ManualTaskParser.CssClassContext;
import io.resys.hdes.ast.ManualTaskParser.DebugValueContext;
import io.resys.hdes.ast.ManualTaskParser.DefaultValueContext;
import io.resys.hdes.ast.ManualTaskParser.DescriptionContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownArgContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownArgsContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownKeyAndValueContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownKeysAndValuesContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownTypeContext;
import io.resys.hdes.ast.ManualTaskParser.DropdownsContext;
import io.resys.hdes.ast.ManualTaskParser.FieldArgsContext;
import io.resys.hdes.ast.ManualTaskParser.FieldContext;
import io.resys.hdes.ast.ManualTaskParser.FieldsContext;
import io.resys.hdes.ast.ManualTaskParser.FormContext;
import io.resys.hdes.ast.ManualTaskParser.GroupArgsContext;
import io.resys.hdes.ast.ManualTaskParser.GroupContext;
import io.resys.hdes.ast.ManualTaskParser.GroupsContext;
import io.resys.hdes.ast.ManualTaskParser.IdContext;
import io.resys.hdes.ast.ManualTaskParser.InputsContext;
import io.resys.hdes.ast.ManualTaskParser.LiteralContext;
import io.resys.hdes.ast.ManualTaskParser.MessageContext;
import io.resys.hdes.ast.ManualTaskParser.MtContext;
import io.resys.hdes.ast.ManualTaskParser.ObjectTypeContext;
import io.resys.hdes.ast.ManualTaskParser.ScalarTypeContext;
import io.resys.hdes.ast.ManualTaskParser.SimpleTypeContext;
import io.resys.hdes.ast.ManualTaskParser.StatementContext;
import io.resys.hdes.ast.ManualTaskParser.StatementTypeContext;
import io.resys.hdes.ast.ManualTaskParser.StatementsArgsContext;
import io.resys.hdes.ast.ManualTaskParser.StatementsContext;
import io.resys.hdes.ast.ManualTaskParser.ThenContext;
import io.resys.hdes.ast.ManualTaskParser.TypeDefArgsContext;
import io.resys.hdes.ast.ManualTaskParser.TypeDefContext;
import io.resys.hdes.ast.ManualTaskParser.TypeDefsContext;
import io.resys.hdes.ast.ManualTaskParser.TypeNameContext;
import io.resys.hdes.ast.ManualTaskParser.WhenContext;
import io.resys.hdes.ast.ManualTaskParserBaseVisitor;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.ArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TypeDefNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.ImmutableArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.ImmutableDropdown;
import io.resys.hdes.ast.api.nodes.ImmutableDropdownField;
import io.resys.hdes.ast.api.nodes.ImmutableFields;
import io.resys.hdes.ast.api.nodes.ImmutableGroup;
import io.resys.hdes.ast.api.nodes.ImmutableGroups;
import io.resys.hdes.ast.api.nodes.ImmutableLiteral;
import io.resys.hdes.ast.api.nodes.ImmutableLiteralField;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskBody;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskDropdowns;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskForm;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskInputs;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskStatements;
import io.resys.hdes.ast.api.nodes.ImmutableObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.ImmutableScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.ImmutableStatement;
import io.resys.hdes.ast.api.nodes.ImmutableThenStatement;
import io.resys.hdes.ast.api.nodes.ImmutableWhenStatement;
import io.resys.hdes.ast.api.nodes.ManualTaskNode;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Dropdown;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Fields;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.FormBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.FormField;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Group;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Groups;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskDropdowns;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskForm;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskInputs;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskStatements;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Statement;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.StatementType;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ThenStatement;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.WhenStatement;
import io.resys.hdes.ast.spi.visitors.ast.FwParserAstNodeVisitor.FwRedundentTypeName;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class MtParserAstNodeVisitor extends ManualTaskParserBaseVisitor<AstNode> {

  private final TokenIdGenerator tokenIdGenerator;

  public MtParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
  }
  
  @Value.Immutable
  public interface MtRedundentId extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface MtRedundentDescription extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface MtRedundentTypeName extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface MtRedundentScalarType extends ManualTaskNode {
    ScalarType getValue();
  }
  @Value.Immutable
  public interface MtRedundentDropdownType extends ManualTaskNode {
    Boolean getMultiple();
  }
  @Value.Immutable
  public interface MtRedundentInputArgs extends ManualTaskNode {
    List<TypeDefNode> getValues();
  }
  @Value.Immutable
  public interface MtRedundentDropdownArgs extends ManualTaskNode {
    List<Dropdown> getValues();
  }  
  @Value.Immutable
  public interface MtRedundentDropdownKeysAndValues extends ManualTaskNode {
    Map<String, String> getValues();
  }  
  @Value.Immutable
  public interface MtRedundentDropdownKeyAndValue extends ManualTaskNode {
    String getKey();
    String getValue();
  }    
  @Value.Immutable
  public interface MtRedundentFieldArgs extends ManualTaskNode {
    List<FormField> getValues();
  }   
  @Value.Immutable
  public interface MtRedundentGroupArgs extends ManualTaskNode {
    List<Group> getValues();
  }
  @Value.Immutable
  public interface MtRedundentDropdown extends ManualTaskNode {
    MtRedundentDropdownType getType();
    String getRefName();
  }
  @Value.Immutable
  public interface MtRedundentCssClass extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface MtRedundentDefaultValue extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface MtRedundentStatementArgs extends ManualTaskNode {
    List<Statement> getValues();
  }
  @Value.Immutable
  public interface MtRedundentStatementType extends ManualTaskNode {
    StatementType getValue();
  }
  @Value.Immutable
  public interface MtRedundentStatementMsg extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface MtRedundentDebugValue extends ManualTaskNode {
    String getValue();
  }
  
  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    return literal(ctx, token(ctx));
  }
  @Override
  public ManualTaskNode visitId(IdContext ctx) {
    return ImmutableMtRedundentId.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(MtRedundentTypeName.class).get().getValue())
        .build();
  }
  @Override
  public ManualTaskNode visitDescription(DescriptionContext ctx) {
    return ImmutableMtRedundentDescription.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(Literal.class).get().getValue())
        .build();
  }
  @Override
  public ManualTaskNode visitTypeName(TypeNameContext ctx) {
    return ImmutableMtRedundentTypeName.builder()
        .token(token(ctx))
        .value(ctx.getText())
        .build();
  }
  @Override
  public MtRedundentScalarType visitScalarType(ScalarTypeContext ctx) {
    return ImmutableMtRedundentScalarType.builder()
        .token(token(ctx))
        .value(ScalarType.valueOf(ctx.getText()))
        .build();
  }
  @Override
  public MtRedundentDropdownType visitDropdownType(DropdownTypeContext ctx) {
    TerminalNode node = (TerminalNode) ctx.getChild(0);
    return ImmutableMtRedundentDropdownType.builder()
        .token(token(ctx))
        .multiple(node.getSymbol().getType() == ManualTaskParser.DROPDOWN_MULTIPLE)
        .build();
  }
  @Override
  public ManualTaskBody visitMt(MtContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableManualTaskBody.builder()
        .token(token(ctx))
        .id(nodes.of(MtRedundentId.class).get().getValue())
        .description(nodes.of(MtRedundentDescription.class).get().getValue())
        .form(nodes.of(ManualTaskForm.class).get())
        .dropdowns(nodes.of(ManualTaskDropdowns.class).get())
        .statements(nodes.of(ManualTaskStatements.class).get())
        .inputs(nodes.of(ManualTaskInputs.class).get())
        .build();
  }
  @Override
  public ManualTaskInputs visitInputs(InputsContext ctx) {
    Nodes nodes = nodes(ctx);
    List<TypeDefNode> values = nodes.of(MtRedundentInputArgs.class)
        .map(a -> a.getValues()).orElse(Collections.emptyList());
    return ImmutableManualTaskInputs.builder()
        .token(token(ctx))
        .values(values)
        .build();
  }
  
  @Override
  public AstNode visitCssClass(CssClassContext ctx) {
    Literal literal = literal(ctx, token(ctx));
    return ImmutableMtRedundentCssClass.builder()
        .token(token(ctx))
        .value(literal.getValue())
        .build();
  }
  @Override
  public AstNode visitTypeDefs(TypeDefsContext ctx) {
    return nodes(ctx).of(ImmutableMtRedundentInputArgs.class).orElseGet(() -> ImmutableMtRedundentInputArgs.builder()
        .token(token(ctx))
        .build());
  }

  @Override
  public AstNode visitTypeDefArgs(TypeDefArgsContext ctx) {
    return ImmutableMtRedundentInputArgs.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(TypeDefNode.class))
        .build();
  }

  @Override
  public AstNode visitTypeDef(TypeDefContext ctx) {
    ParseTree c = ctx.getChild(1);
    return c.accept(this);
  }

  @Override
  public MtRedundentDebugValue visitDebugValue(DebugValueContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentDebugValue.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }
  
  @Override
  public ScalarTypeDefNode visitSimpleType(SimpleTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableScalarTypeDefNode.builder()
        .token(token(ctx))
        .required(isRequiredInputType(ctx))
        .type(nodes.of(MtRedundentScalarType.class).get().getValue())
        .name(nodes.of(MtRedundentTypeName.class).get().getValue())
        .debugValue(nodes.of(MtRedundentDebugValue.class).get().getValue())
        .build();
  }
  @Override
  public ArrayTypeDefNode visitArrayType(ArrayTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableArrayTypeDefNode.builder()
        .token(token(ctx))
        .required(isRequiredInputType(ctx))
        .value(nodes.of(TypeDefNode.class).get())
        .build();
  }
  @Override
  public ObjectTypeDefNode visitObjectType(ObjectTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    List<TypeDefNode> values = nodes.of(FlowInputs.class).map((FlowInputs i)-> i.getValues())
        .orElse(Collections.emptyList()); 
    return ImmutableObjectTypeDefNode.builder()
        .token(token(ctx))
        .required(isRequiredInputType(ctx))
        .name(nodes.of(FwRedundentTypeName.class).get().getValue())
        .values(values)
        .build();
  }
  
  @Override
  public ManualTaskDropdowns visitDropdowns(DropdownsContext ctx) {
    Nodes nodes = nodes(ctx);
    List<Dropdown> values = nodes.of(MtRedundentDropdownArgs.class)
        .map((MtRedundentDropdownArgs i)-> i.getValues())
        .orElse(Collections.emptyList()); 
    return ImmutableManualTaskDropdowns.builder()
        .token(token(ctx))
        .values(values)
        .build();
  }
  @Override
  public MtRedundentDropdownArgs visitDropdownArgs(DropdownArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentDropdownArgs.builder()
        .token(token(ctx))
        .values(nodes.list(Dropdown.class))
        .build();
  }
  @Override
  public Dropdown visitDropdownArg(DropdownArgContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableDropdown.builder()
        .token(token(ctx))
        .name(nodes.of(MtRedundentTypeName.class).get().getValue())
        .values(nodes.of(MtRedundentDropdownKeysAndValues.class).get().getValues())
        .build();
  }
  @Override
  public MtRedundentDropdownKeysAndValues visitDropdownKeysAndValues(DropdownKeysAndValuesContext ctx) {
    Nodes nodes = nodes(ctx);
    Map<String, String> values = nodes.list(MtRedundentDropdownKeyAndValue.class).stream()
    .collect(Collectors.toMap(v -> v.getKey(), v-> v.getValue()));
    return ImmutableMtRedundentDropdownKeysAndValues.builder()
        .token(token(ctx))
        .values(values)
        .build();
  }
  @Override
  public MtRedundentDropdownKeyAndValue visitDropdownKeyAndValue(DropdownKeyAndValueContext ctx) {
    Literal key = (Literal) ctx.getChild(0).accept(this);
    Literal value = (Literal) ctx.getChild(2).accept(this);
    return ImmutableMtRedundentDropdownKeyAndValue.builder()
        .token(token(ctx))
        .key(key.getValue())
        .value(value.getValue())
        .build();
  }

  @Override
  public Fields visitFields(FieldsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableFields.builder()
        .token(token(ctx))
        .values(nodes.of(MtRedundentFieldArgs.class).map(a -> a.getValues()).orElse(Collections.emptyList()))
        .build();
  }

  @Override
  public MtRedundentFieldArgs visitFieldArgs(FieldArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentFieldArgs.builder()
        .token(token(ctx))
        .values(nodes.list(FormField.class))
        .build();
  }

  @Override
  public Groups visitGroups(GroupsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableGroups.builder()
        .token(token(ctx))
        .values(nodes.of(MtRedundentGroupArgs.class).map(e -> e.getValues()).orElse(Collections.emptyList()))
        .build();
  }

  @Override
  public MtRedundentGroupArgs visitGroupArgs(GroupArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentGroupArgs.builder()
        .token(token(ctx))
        .values(nodes.list(Group.class))
        .build();
  }

  @Override
  public Group visitGroup(GroupContext ctx) {
    Nodes nodes = nodes(ctx);
    String id = nodes.of(MtRedundentId.class).get().getValue();
    
    Optional<Fields> fields = nodes.of(Fields.class);
    if(fields.isPresent()) {
      return ImmutableGroup.builder()
          .token(token(ctx))
          .id(id)
          .value(fields.get())
          .build();
    }
    Optional<Groups> groups = nodes.of(Groups.class);
    return ImmutableGroup.builder()
        .token(token(ctx))
        .id(id)
        .value(groups.get())
        .build();
  }

  @Override
  public FormField visitField(FieldContext ctx) {
    Nodes nodes = nodes(ctx);
    boolean required = ((TerminalNode) ctx.getChild(0)).getSymbol().getType() == ManualTaskParser.REQUIRED;
    ScalarType scalarType = nodes.of(MtRedundentScalarType.class).get().getValue();
    String typeName = nodes.of(MtRedundentTypeName.class).get().getValue();
    Optional<MtRedundentDropdown> dropdown = nodes.of(MtRedundentDropdown.class);
    Optional<String> defaultValue = nodes.of(MtRedundentDefaultValue.class).map(e -> e.getValue());
    Optional<String> cssClasses = nodes.of(MtRedundentCssClass.class).map(e -> e.getValue());
    
    
    if(dropdown.isPresent()) {
      return ImmutableDropdownField.builder()
          .token(token(ctx))
          .required(required)
          .cssClasses(cssClasses)
          .defaultValue(defaultValue)
          .source(dropdown.get().getRefName())
          .multiple(dropdown.get().getType().getMultiple())
          .type(scalarType)
          .name(typeName)
          .build();
    }
    
    return ImmutableLiteralField.builder()
        .token(token(ctx))
        .required(required)
        .cssClasses(cssClasses)
        .defaultValue(defaultValue)
        .type(scalarType)
        .name(typeName)
        .build();
  }

  @Override
  public MtRedundentDropdown visitDropdown(DropdownContext ctx) {
    Nodes nodes = nodes(ctx);
    String typeName = nodes.of(MtRedundentTypeName.class).get().getValue();
    return ImmutableMtRedundentDropdown.builder()
        .token(token(ctx))
        .type(nodes.of(MtRedundentDropdownType.class).get())
        .refName(typeName)
        .build();
  }

  @Override
  public ManualTaskStatements visitStatements(StatementsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableManualTaskStatements.builder()
        .token(token(ctx))
        .values(nodes.of(MtRedundentStatementArgs.class).map(s -> s.getValues()).orElse(Collections.emptyList()))
        .build();
  }
  

  @Override
  public MtRedundentStatementArgs visitStatementsArgs(StatementsArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentStatementArgs.builder()
        .token(token(ctx))
        .values(nodes.list(Statement.class))
        .build();
  }
  
  @Override
  public Statement visitStatement(StatementContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableStatement.builder()
        .token(token(ctx))
        .name(nodes.of(MtRedundentTypeName.class).get().getValue())
        .when(nodes.of(WhenStatement.class).get())
        .then(nodes.of(ThenStatement.class).get())
        .build();
  }

  @Override
  public WhenStatement visitWhen(WhenContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenStatement.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }

  @Override
  public ThenStatement visitThen(ThenContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableThenStatement.builder()
        .token(token(ctx))
        .type(nodes.of(MtRedundentStatementType.class).get().getValue())
        .message(nodes.of(MtRedundentStatementMsg.class).get().getValue())
        .build();
  }

  @Override
  public MtRedundentStatementType visitStatementType(StatementTypeContext ctx) {
    TerminalNode node = (TerminalNode) ctx.getChild(0);
    StatementType type;
    switch (node.getSymbol().getType()) {
    case ManualTaskParser.ALERT: type = StatementType.ALERT; break;
    case ManualTaskParser.SHOW: type = StatementType.SHOW; break;
    case ManualTaskParser.EVALUATE: type = StatementType.ALERT; break;
    default: throw new AstNodeException("Unknown statement type: " + ctx.getText() + "!");
    }
    return ImmutableMtRedundentStatementType.builder()
        .token(token(ctx))
        .value(type)
        .build();
  }
  
  @Override
  public MtRedundentStatementMsg visitMessage(MessageContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentStatementMsg.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }
  @Override
  public ManualTaskForm visitForm(FormContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableManualTaskForm.builder()
        .token(token(ctx))
        .value(nodes.of(FormBody.class))
        .build();
  }

  @Override
  public MtRedundentDefaultValue visitDefaultValue(DefaultValueContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentDefaultValue.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }

  private boolean isRequiredInputType(ParserRuleContext ctx) {
    TerminalNode requirmentType = (TerminalNode) ctx.getParent().getChild(0);
    return requirmentType.getSymbol().getType() == FlowParser.REQUIRED;
  }

  private Nodes nodes(ParserRuleContext node) {
    return Nodes.from(node, this);
  }

  private AstNode.Token token(ParserRuleContext node) {
    return Nodes.token(node, tokenIdGenerator);
  }
  
  private Literal literal(ParserRuleContext ctx, AstNode.Token token) {
    String value = ctx.getText();
    ScalarType type = null;
    TerminalNode terminalNode = (TerminalNode) ctx.getChild(0);
    switch (terminalNode.getSymbol().getType()) {
    case ManualTaskParser.StringLiteral:
      type = ScalarType.STRING;
      value = Nodes.getStringLiteralValue(ctx);
      break;
    case ManualTaskParser.BooleanLiteral:
      type = ScalarType.BOOLEAN;
      break;
    case ManualTaskParser.DecimalLiteral:
      type = ScalarType.DECIMAL;
      break;
    case ManualTaskParser.IntegerLiteral:
      type = ScalarType.INTEGER;
      value = value.replaceAll("_", "");
      break;
    default:
      throw new AstNodeException("Unknown literal: " + ctx.getText() + "!");
    }
    return ImmutableLiteral.builder()
        .token(token)
        .type(type)
        .value(value)
        .build();
  }
}

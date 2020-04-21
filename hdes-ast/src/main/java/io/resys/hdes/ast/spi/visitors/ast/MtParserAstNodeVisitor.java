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
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.HdesParser.ActionBodyThenContext;
import io.resys.hdes.ast.HdesParser.ActionBodyWhenContext;
import io.resys.hdes.ast.HdesParser.ActionContext;
import io.resys.hdes.ast.HdesParser.ActionTypeContext;
import io.resys.hdes.ast.HdesParser.ActionsArgsContext;
import io.resys.hdes.ast.HdesParser.ActionsContext;
import io.resys.hdes.ast.HdesParser.CssClassContext;
import io.resys.hdes.ast.HdesParser.DefaultValueContext;
import io.resys.hdes.ast.HdesParser.DropdownArgContext;
import io.resys.hdes.ast.HdesParser.DropdownArgsContext;
import io.resys.hdes.ast.HdesParser.DropdownContext;
import io.resys.hdes.ast.HdesParser.DropdownKeyAndValueContext;
import io.resys.hdes.ast.HdesParser.DropdownKeysAndValuesContext;
import io.resys.hdes.ast.HdesParser.DropdownTypeContext;
import io.resys.hdes.ast.HdesParser.DropdownsContext;
import io.resys.hdes.ast.HdesParser.FieldArgsContext;
import io.resys.hdes.ast.HdesParser.FieldContext;
import io.resys.hdes.ast.HdesParser.FieldsContext;
import io.resys.hdes.ast.HdesParser.FormContext;
import io.resys.hdes.ast.HdesParser.GroupArgsContext;
import io.resys.hdes.ast.HdesParser.GroupContext;
import io.resys.hdes.ast.HdesParser.GroupsContext;
import io.resys.hdes.ast.HdesParser.MessageContext;
import io.resys.hdes.ast.HdesParser.MtBodyContext;
import io.resys.hdes.ast.HdesParser.TypeDefContext;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.DirectionType;
import io.resys.hdes.ast.api.nodes.AstNode.Headers;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.ImmutableDropdown;
import io.resys.hdes.ast.api.nodes.ImmutableDropdownField;
import io.resys.hdes.ast.api.nodes.ImmutableFields;
import io.resys.hdes.ast.api.nodes.ImmutableGroup;
import io.resys.hdes.ast.api.nodes.ImmutableGroups;
import io.resys.hdes.ast.api.nodes.ImmutableLiteralField;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskAction;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskActions;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskBody;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskDropdowns;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskForm;
import io.resys.hdes.ast.api.nodes.ImmutableManualTaskInputs;
import io.resys.hdes.ast.api.nodes.ImmutableThenAction;
import io.resys.hdes.ast.api.nodes.ImmutableWhenAction;
import io.resys.hdes.ast.api.nodes.ManualTaskNode;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ActionType;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Dropdown;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Fields;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.FormBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.FormField;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Group;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.Groups;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskAction;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskActions;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskDropdowns;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskForm;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ThenAction;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.WhenAction;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentDescription;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentId;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentScalarType;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentTypeName;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class MtParserAstNodeVisitor extends DtParserAstNodeVisitor {

  public MtParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super(tokenIdGenerator);
  }

  @Value.Immutable
  public interface MtRedundentDropdownType extends ManualTaskNode {
    Boolean getMultiple();
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
  public interface MtRedundentActionArgs extends ManualTaskNode {
    List<ManualTaskAction> getValues();
  }
  @Value.Immutable
  public interface MtRedundentActionType extends ManualTaskNode {
    ActionType getValue();
  }
  @Value.Immutable
  public interface MtRedundentActionMsg extends ManualTaskNode {
    String getValue();
  }

  @Override
  public MtRedundentDropdownType visitDropdownType(DropdownTypeContext ctx) {
    TerminalNode node = (TerminalNode) ctx.getChild(0);
    return ImmutableMtRedundentDropdownType.builder()
        .token(token(ctx))
        .multiple(node.getSymbol().getType() == HdesParser.DROPDOWN_MULTIPLE)
        .build();
  }
  
  protected final RedundentTypeName getDefTypeName(ParserRuleContext ctx) {
    if(ctx.getParent() instanceof TypeDefContext) {
      return (RedundentTypeName) ctx.getParent().getChild(0).accept(this);
    }
    return (RedundentTypeName) ctx.getParent().getParent().getChild(0).accept(this);
  }
  
  
  @Override
  public ManualTaskBody visitMtBody(MtBodyContext ctx) {
    Nodes nodes = nodes(ctx);
    Headers headers = nodes.of(Headers.class).get();
    
    return ImmutableManualTaskBody.builder()
        .token(token(ctx))
        .id(nodes.of(RedundentTypeName.class).get().getValue())
        .description(nodes.of(RedundentDescription.class).get().getValue())
        .form(nodes.of(ManualTaskForm.class).get())
        .dropdowns(nodes.of(ManualTaskDropdowns.class).get())
        .actions(nodes.of(ManualTaskActions.class).get())
        .inputs(ImmutableManualTaskInputs.builder()
            .token(token(ctx))
            .values(headers.getValues().stream()
                .filter(t -> t.getDirection() == DirectionType.IN).collect(Collectors.toList()))
            .build())
        .build();
  }

  @Override
  public AstNode visitCssClass(CssClassContext ctx) {
    Literal literal = Nodes.literal(ctx, token(ctx));
    return ImmutableMtRedundentCssClass.builder()
        .token(token(ctx))
        .value(literal.getValue())
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
        .name(nodes.of(RedundentTypeName.class).get().getValue())
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
    String id = nodes.of(RedundentId.class).get().getValue();
    
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
    boolean required = ((TerminalNode) ctx.getChild(2)).getSymbol().getType() == HdesParser.REQUIRED;
    ScalarType scalarType = nodes.of(RedundentScalarType.class).get().getValue();
    String typeName = nodes.of(RedundentTypeName.class).get().getValue();
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
    String typeName = nodes.of(RedundentTypeName.class).get().getValue();
    return ImmutableMtRedundentDropdown.builder()
        .token(token(ctx))
        .type(nodes.of(MtRedundentDropdownType.class).get())
        .refName(typeName)
        .build();
  }

  @Override
  public ManualTaskActions visitActions(ActionsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableManualTaskActions.builder()
        .token(token(ctx))
        .values(nodes.of(MtRedundentActionArgs.class).map(s -> s.getValues()).orElse(Collections.emptyList()))
        .build();
  }
  

  @Override
  public MtRedundentActionArgs visitActionsArgs(ActionsArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentActionArgs.builder()
        .token(token(ctx))
        .values(nodes.list(ManualTaskAction.class))
        .build();
  }
  
  @Override
  public ManualTaskAction visitAction(ActionContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableManualTaskAction.builder()
        .token(token(ctx))
        .name(nodes.of(RedundentTypeName.class).get().getValue())
        .when(nodes.of(WhenAction.class).get())
        .then(nodes.of(ThenAction.class).get())
        .build();
  }

  @Override
  public WhenAction visitActionBodyWhen(ActionBodyWhenContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableWhenAction.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }

  @Override
  public ThenAction visitActionBodyThen(ActionBodyThenContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableThenAction.builder()
        .token(token(ctx))
        .type(nodes.of(MtRedundentActionType.class).get().getValue())
        .message(nodes.of(MtRedundentActionMsg.class).get().getValue())
        .build();
  }

  @Override
  public MtRedundentActionType visitActionType(ActionTypeContext ctx) {
    TerminalNode node = (TerminalNode) ctx.getChild(0);
    ActionType type;
    switch (node.getSymbol().getType()) {
    case HdesParser.ALERT: type = ActionType.ALERT; break;
    case HdesParser.SHOW: type = ActionType.SHOW; break;
    case HdesParser.EVALUATE: type = ActionType.ALERT; break;
    default: throw new AstNodeException("Unknown Action type: " + ctx.getText() + "!");
    }
    return ImmutableMtRedundentActionType.builder()
        .token(token(ctx))
        .value(type)
        .build();
  }
  
  @Override
  public MtRedundentActionMsg visitMessage(MessageContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMtRedundentActionMsg.builder()
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
}

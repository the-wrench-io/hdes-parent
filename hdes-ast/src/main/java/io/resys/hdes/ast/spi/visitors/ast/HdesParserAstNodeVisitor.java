package io.resys.hdes.ast.spi.visitors.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.HdesParser.ArrayTypeContext;
import io.resys.hdes.ast.HdesParser.DebugValueContext;
import io.resys.hdes.ast.HdesParser.DescriptionContext;
import io.resys.hdes.ast.HdesParser.DirectionTypeContext;
import io.resys.hdes.ast.HdesParser.HdesBodyContext;
import io.resys.hdes.ast.HdesParser.HeadersContext;
import io.resys.hdes.ast.HdesParser.IdContext;
import io.resys.hdes.ast.HdesParser.LiteralContext;
import io.resys.hdes.ast.HdesParser.ObjectTypeContext;
import io.resys.hdes.ast.HdesParser.ScalarTypeContext;
import io.resys.hdes.ast.HdesParser.SimpleTypeContext;
import io.resys.hdes.ast.HdesParser.TypeDefArgsContext;
import io.resys.hdes.ast.HdesParser.TypeDefContext;
import io.resys.hdes.ast.HdesParser.TypeDefsContext;
import io.resys.hdes.ast.HdesParser.TypeNameContext;

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

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.ArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.DirectionType;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TypeDefNode;
import io.resys.hdes.ast.api.nodes.ImmutableArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.ImmutableHeaders;
import io.resys.hdes.ast.api.nodes.ImmutableObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.ImmutableScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.ManualTaskNode;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;


public class HdesParserAstNodeVisitor extends FwParserAstNodeVisitor {

  // Internal only
  @Value.Immutable
  public interface RedundentId extends AstNode {
    String getValue();
  }
  @Value.Immutable
  public interface RedundentDescription extends AstNode {
    String getValue();
  }
  @Value.Immutable
  public interface RedundentTypeName extends AstNode {
    String getValue();
  }
  @Value.Immutable
  public interface RedundentScalarType extends AstNode {
    ScalarType getValue();
  }
  @Value.Immutable
  public interface RedundentDebugValue extends ManualTaskNode {
    String getValue();
  }
  @Value.Immutable
  public interface RedundentDirection extends AstNode {
    DirectionType getValue();
  }
  @Value.Immutable
  public interface RedundentTypeDefArgs extends ManualTaskNode {
    List<TypeDefNode> getValues();
  }
  
  public HdesParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super(tokenIdGenerator);
  }

  @Override
  public AstNode visitHdesBody(HdesBodyContext ctx) {
    ParseTree c = ctx.getChild(3);
    return c.accept(this);
  }
  
  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    return Nodes.literal(ctx, token(ctx));
  }

  @Override
  public RedundentId visitId(IdContext ctx) {
    return ImmutableRedundentId.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(RedundentTypeName.class).get().getValue())
        .build();
  }

  @Override
  public RedundentDescription visitDescription(DescriptionContext ctx) {
    return ImmutableRedundentDescription.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(Literal.class).get().getValue())
        .build();
  }

  @Override
  public RedundentTypeName visitTypeName(TypeNameContext ctx) {
    return ImmutableRedundentTypeName.builder()
        .token(token(ctx))
        .value(ctx.getText())
        .build();
  }
  
  @Override
  public RedundentScalarType visitScalarType(ScalarTypeContext ctx) {
    return ImmutableRedundentScalarType.builder()
        .token(token(ctx))
        .value(ScalarType.valueOf(ctx.getText()))
        .build();
  }
  @Override
  public AstNode visitHeaders(HeadersContext ctx) {
    return nodes(ctx).of(RedundentTypeDefArgs.class)
        .map(args -> ImmutableHeaders.builder().token(token(ctx)).values(args.getValues()).build())
        .orElse(ImmutableHeaders.builder().token(token(ctx)).build());
  }

  @Override
  public RedundentDirection visitDirectionType(DirectionTypeContext ctx) {
    return ImmutableRedundentDirection.builder()
        .token(token(ctx))
        .value(DirectionType.valueOf(ctx.getText()))
        .build();
  }
  
  @Override
  public AstNode visitTypeDefs(TypeDefsContext ctx) {
    return nodes(ctx).of(RedundentTypeDefArgs.class).orElseGet(() -> ImmutableRedundentTypeDefArgs.builder()
        .token(token(ctx))
        .build());
  }

  @Override
  public AstNode visitTypeDefArgs(TypeDefArgsContext ctx) {
    return ImmutableRedundentTypeDefArgs.builder()
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
  public ScalarTypeDefNode visitSimpleType(SimpleTypeContext ctx) {
    TerminalNode requirmentType = (TerminalNode) ctx.getChild(1);
    Nodes nodes = nodes(ctx);
    return ImmutableScalarTypeDefNode.builder()
        .token(token(ctx))
        .required(requirmentType.getSymbol().getType() == HdesParser.REQUIRED)
        .name(getDefTypeName(ctx).getValue())
        .type(nodes.of(RedundentScalarType.class).get().getValue())
        .direction(nodes.of(RedundentDirection.class).get().getValue())
        .debugValue(nodes.of(RedundentDebugValue.class).map(e -> e.getValue()))
        .build();
  }
  
  @Override
  public ArrayTypeDefNode visitArrayType(ArrayTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    TypeDefNode input = nodes.of(TypeDefNode.class).get();
    
    return ImmutableArrayTypeDefNode.builder()
        .token(token(ctx))
        .required(input.getRequired())
        .name(input.getName())
        .direction(input.getDirection())
        .value(input)
        .build();
  }

  @Override
  public ObjectTypeDefNode visitObjectType(ObjectTypeContext ctx) {
    Nodes nodes = nodes(ctx);
    List<TypeDefNode> values = nodes.of(RedundentTypeDefArgs.class).map((RedundentTypeDefArgs i)-> i.getValues())
        .orElse(Collections.emptyList());
    TerminalNode requirmentType = (TerminalNode) ctx.getChild(1);
    
    return ImmutableObjectTypeDefNode.builder()
        .token(token(ctx))
        .required(requirmentType.getSymbol().getType() == HdesParser.REQUIRED)
        .name(getDefTypeName(ctx).getValue())
        .direction(nodes.of(RedundentDirection.class).get().getValue())
        .values(values)
        .build();
  }

  @Override
  public RedundentDebugValue visitDebugValue(DebugValueContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableRedundentDebugValue.builder()
        .token(token(ctx))
        .value(nodes.of(Literal.class).get().getValue())
        .build();
  }
}

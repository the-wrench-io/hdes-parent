package io.resys.hdes.ast.api.nodes;

import java.util.List;
import java.util.Optional;

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

import org.immutables.value.Value;

public interface DecisionTableNode extends AstNode {
  
  interface HitPolicy extends DecisionTableNode {}
  interface RuleValue extends DecisionTableNode {}
  
  @Value.Immutable
  interface DecisionTableBody extends DecisionTableNode, BodyNode {
    Optional<String> getDescription();
    HitPolicy getHitPolicy();
    Headers getHeaders();
  }
 
  @Value.Immutable
  interface HitPolicyAll extends HitPolicy {
    List<RuleRow> getRows();
  }

  @Value.Immutable
  interface HitPolicyMatrix extends HitPolicy {
    ScalarType getFromType();
    ScalarType getToType();
    List<Rule> getRules();
    List<MatrixRow> getRows();
  }

  @Value.Immutable
  interface MatrixRow extends DecisionTableNode {
    TypeName getTypeName();
    List<Literal> getValues();
  }

  @Value.Immutable
  interface HitPolicyFirst extends HitPolicy {
    List<RuleRow> getRows();
  }
  
  @Value.Immutable
  interface RuleRow extends DecisionTableNode {
    List<Rule> getRules();
    String getText();
  }
  
  @Value.Immutable
  interface Rule extends DecisionTableNode {
    int getHeader();
    RuleValue getValue();
  }
  
  @Value.Immutable
  interface UndefinedValue extends RuleValue {
    
  }
  
  @Value.Immutable
  interface LiteralValue extends RuleValue {
    Literal getValue();
  }

  @Value.Immutable
  interface NegateLiteralValue extends RuleValue {
    Literal getValue();
  }
  
  @Value.Immutable
  interface ExpressionValue extends RuleValue {
    String getValue();
    AstNode getExpression();
  }
  
  @Value.Immutable
  interface HeaderRefValue extends DecisionTableNode {
  }
  
  @Value.Immutable
  interface InOperation extends DecisionTableNode {
    List<Literal> getValues();
  }
}

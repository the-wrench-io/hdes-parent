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
  
  enum DirectionType { IN, OUT }
  
  interface Rule extends DecisionTableNode {
    int getHeader();
  }
  
  interface HitPolicy extends DecisionTableNode {
    int getPosition();
  }
  
  @Value.Immutable
  interface DecisionTableBody extends DecisionTableNode {
    String getId();
    String getDescription();
    HitPolicy getHitPolicy();
    List<Header> getHeaders();
  }
 
  @Value.Immutable
  interface Header extends DecisionTableNode {
    String getName();
    NodeDataType getDataType();
  }
  
  @Value.Immutable
  interface HitPolicyAll extends HitPolicy {
    List<RuleRow> getRows();
  }

  @Value.Immutable
  interface HitPolicyMatrix extends HitPolicy {
    List<RuleRow> getRows();
  }

  @Value.Immutable
  interface HitPolicyFirst extends HitPolicy {
    RuleRow getValue();
    Optional<HitPolicyFirst> getNext();
  }
  
  @Value.Immutable
  interface RuleRow extends DecisionTableNode {
    List<Rule> getRules();
  }
  
  @Value.Immutable
  interface UndefinedRule extends Rule {
    
  }
  
  @Value.Immutable
  interface LiteralRule extends Rule {
    String getValue();
  }
  
  @Value.Immutable
  interface ExpressionRule extends Rule {
    String getValue();
    ExpressionNode getExpression();
  }
}

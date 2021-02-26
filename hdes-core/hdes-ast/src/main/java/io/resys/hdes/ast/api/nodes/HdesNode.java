package io.resys.hdes.ast.api.nodes;
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

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.immutables.value.Value;

public interface HdesNode extends Serializable {
  Token getToken();
  @Nullable
  HdesNodeType getNodeType();
  
  @Value.Immutable
  public interface ContentNode extends HdesNode {
    List<BodyNode> getValues();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.ROOT_CONTENT; }
  }
  
  @Value.Immutable
  interface ErrorNode {
    String getBodyId();
    HdesNode getTarget();
    Optional<HdesNode> getTargetLink();
    String getMessage();
  }
  
  @Value.Immutable
  interface Token {
    String getText();
    Position getStart();
    @Nullable
    Position getEnd();
  }

  @Value.Immutable
  interface Position {
    int getLine();
    int getCol();
  }
  
  @Value.Immutable
  interface EmptyNode extends HdesNode { }
  
  enum HdesNodeType {
    ROOT, ROOT_CONTENT,
    BODY_DT, BODY_SE, BODY_FL, BODY_EX, BODY_EMPTY, BODY_ID,
    
    TYPES, TYPE_LITERAL, TYPE_OBJECT, TYPE_SCALAR,
    
    INVOCATION_NAMED_PLACEHOLDER, INVOCATION_EMPTY_PLACEHOLDER,
    INVOCATION_SIMPLE, INVOCATION_NESTED, 
    INVOCATION_SORTBY, INVOCATION_SORTBY_DEF,
    
    MAPPING_OBJECT, MAPPING_FIELD, MAPPING_EXP, MAPPING_ARRAY, MAPPING_FAST,
    
    SE_COMMAND, SE_PROMISE,
    
    FLOW_STEP, FLOW_STEP_AS,
    FLOW_STEP_CALL_DEF, FLOW_CALL_DEF,
    FLOW_ACTION_EMPTY, FLOW_ACTION_CALL, FLOW_ACTION_ITERATE,
    FLOW_POINTER_SPLIT, FLOW_POINTER_WHEN, FLOW_POINTER_THEN, FLOW_POINTER_END, FLOW_POINTER_ITERATE_END,
    
    DT_HITPOLICY_FIRST, DT_HITPOLICY_ALL,
    DT_RULE_WHEN_THEN, DT_RULE_WHEN, DT_RULE_THEN,
    DT_HITPOLICY_MAPPING, DT_RULE_MAPPING,
    
    EX_UNARY_NOT, EX_UNARY_NEGATE, EX_UNARY_POS,
    EX_EQUALITY, EX_CONDITIONAL, EX_BETWEEN,
    EX_IN, EX_AND, EX_OR,
    EX_ADDITIVE, EX_MULTIPLICATIVE,
    EX_INSTANCE_STATIC_METHOD, EX_INSTANCE_METHOD,
    EX_LAMBDA, EX_LAMBDA_SORT, EX_LAMBDA_FILTER,
  }
}

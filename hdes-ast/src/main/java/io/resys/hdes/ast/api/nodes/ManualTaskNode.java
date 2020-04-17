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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;


public interface ManualTaskNode extends AstNode {
  
  enum StatementType { SHOW, ALERT, EVALUATE }
  
  interface FormBody extends ManualTaskNode {}
  
  @Value.Immutable
  interface ManualTaskBody extends ManualTaskNode, BodyNode {
    String getDescription();
    ManualTaskInputs getInputs();
    ManualTaskDropdowns getDropdowns();
    ManualTaskStatements getStatements();
    ManualTaskForm getForm();
  }
  
  @Value.Immutable
  interface ManualTaskInputs extends ManualTaskNode {
    List<InputNode> getValues();
  }  
  @Value.Immutable
  interface ManualTaskDropdowns extends ManualTaskNode {
    List<Dropdown> getValues();
  }  
  @Value.Immutable
  interface ManualTaskStatements extends ManualTaskNode {
    List<Statement> getValues();
  }  
  @Value.Immutable
  interface ManualTaskForm extends ManualTaskNode {
    Optional<FormBody> getValue();
  }
  @Value.Immutable
  interface Dropdown extends ManualTaskNode {
    String getName();
    Map<String, String> getValues();
  }
  
  @Value.Immutable
  interface Statement extends ManualTaskNode {
    String getName();
    WhenStatement getWhen();
    ThenStatement getThen();
  }
  
  @Value.Immutable
  interface WhenStatement extends ManualTaskNode {
    String getValue();
    Optional<AstNode> getExpression();
  }
  
  @Value.Immutable
  interface ThenStatement extends ManualTaskNode {
    Optional<String> getMessage();
    StatementType getType();
  }
  
  @Value.Immutable
  interface Group extends FormBody {
    String getId();
    FormBody getValue();
  }

  @Value.Immutable
  interface Groups extends FormBody {
    List<Group> getValues();
  }
  
  @Value.Immutable
  interface Fields extends FormBody {
    List<FormField> getValues();
  }

  interface FormField extends ManualTaskNode {
    Boolean getRequired();
    String getName();
    ScalarType getType();
    Optional<String> getDefaultValue();
    Optional<String> getCssClasses();
  }
  
  @Value.Immutable
  interface DropdownField extends FormField {
    Boolean getMultiple();
    String getSource();
  }
  
  @Value.Immutable
  interface LiteralField extends FormField {
    
  }
}

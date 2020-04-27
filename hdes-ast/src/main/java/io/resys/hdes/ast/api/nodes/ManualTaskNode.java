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
  
  enum ActionMessageType { ERROR, INFO, WARNING }
  
  interface ThenAction extends ManualTaskNode { }
  interface FormBody extends ManualTaskNode {}
  
  @Value.Immutable
  interface ManualTaskBody extends ManualTaskNode, BodyNode {
    String getDescription();
    ManualTaskInputs getInputs();
    ManualTaskDropdowns getDropdowns();
    ManualTaskActions getActions();
    ManualTaskForm getForm();
  }
  
  @Value.Immutable
  interface ManualTaskInputs extends ManualTaskNode {
    List<TypeDefNode> getValues();
  }  
  @Value.Immutable
  interface ManualTaskDropdowns extends ManualTaskNode {
    List<Dropdown> getValues();
  }  
  @Value.Immutable
  interface ManualTaskActions extends ManualTaskNode {
    List<ManualTaskAction> getValues();
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
  interface ManualTaskAction extends ManualTaskNode {
    WhenAction getWhen();
    ThenAction getThen();
  }
  
  @Value.Immutable
  interface WhenAction extends ManualTaskNode {
    String getId();
    ExpressionNode getValue();
  }
  
  @Value.Immutable
  public interface ThenActionShowMsg extends ThenAction {
    String getValue();
    ActionMessageType getMessageType();
  }
  
  @Value.Immutable
  public interface ThenActionShowGroup extends ThenAction {
    String getValue();
  }

  @Value.Immutable
  public interface ThenActionShowField extends ThenAction {
    String getValue();
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

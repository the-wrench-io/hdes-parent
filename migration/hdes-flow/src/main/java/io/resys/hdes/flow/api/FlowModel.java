package io.resys.hdes.flow.api;

/*-
 * #%L
 * hdes-flow
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
import java.util.Collection;
import java.util.Map;

import org.immutables.value.Value;

import io.reactivex.annotations.Nullable;
import io.resys.hdes.datatype.api.DataTypeCommand;

public interface FlowModel extends Serializable, Comparable<FlowModel> {
  FlowModel getParent();

  String getKeyword();

  Map<String, FlowModel> getChildren();

  FlowModel get(String name);

  String getValue();

  Source getSource();

  boolean hasNonNull(String name);

  int getStart();

  int getEnd();

  interface Root extends FlowModel {
    FlowModel getId();

    int getRev();
    
    FlowModel getDescription();

    Collection<InputType> getTypes();

    Map<String, Input> getInputs();

    Map<String, Task> getTasks();
  }

  interface Task extends FlowModel {
    FlowModel getId();

    int getOrder();

    FlowModel getThen();

    Ref getRef();

    Ref getUserTask();

    Ref getDecisionTable();

    Ref getService();

    Map<String, Switch> getSwitch();
  }

  interface Ref extends FlowModel {
    FlowModel getRef();

    FlowModel getCollection();

    FlowModel getInputsNode();

    Map<String, FlowModel> getInputs();
  }

  interface Switch extends FlowModel {
    int getOrder();

    FlowModel getWhen();

    FlowModel getThen();
  }

  interface Input extends FlowModel {
    FlowModel getRequired();

    FlowModel getType();

    FlowModel getDebugValue();
  }

  @Value.Immutable
  interface InputType extends Serializable {
    String getName();

    @Nullable
    String getRef();

    String getValue();
  }

  interface Source extends Serializable {
    int getLine();

    String getValue();

    Collection<DataTypeCommand> getCommands();
  }
}

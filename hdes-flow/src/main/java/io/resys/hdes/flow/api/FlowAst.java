package io.resys.hdes.flow.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.immutables.value.Value;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import io.reactivex.annotations.Nullable;
import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;

@Value.Immutable
public interface FlowAst extends Serializable {
  String getId();

  int getRev();

  Collection<DataType> getInputs();

  @Nullable
  Task getTask();

  interface Task extends Serializable {
    String getId();
    FlowTaskType getType();
    Task getPrevious();
    Collection<Task> getNext();
    Task get(String id);
    @Nullable
    TaskValue getValue();
  }

  @Value.Immutable
  interface TaskValue extends Serializable {
    @Nullable
    String getRef();

    @Nullable
    DataTypeService.Expression getExpression();

    Map<String, String> getInputs();

    boolean isCollection();
  }

  enum FlowTaskType {
    DT, SERVICE, USER_TASK, EMPTY,
    
    // Exclusive - parent gateway for set of decisions
    EXCLUSIVE, DECISION, 
    
    // Merge - waits till multiple gateways finish
    MERGE,
    
    // Final destination
    END
    
    //, PARALLEL,
    
  }
}

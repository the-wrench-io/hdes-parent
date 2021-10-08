package io.resys.hdes.client.api.execution;

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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.TypeDef;

@Value.Immutable
public interface FlowProgram extends Program {
  String getId();
  int getRev();
  Collection<TypeDef> getAcceptDefs();
  Step getStep();
  Collection<Step> getSteps();

  interface Step extends Serializable {
    Step get(String taskId);
    String getId();
    @Nullable
    StepBody getBody();
    FlowTaskType getType();
    Step getPrevious();
    List<Step> getNext();
  }

  @Value.Immutable
  interface StepBody extends Serializable {
    @Nullable
    String getRef();
    @Nullable
    StepExpression getExpression();
    
    Map<String, String> getInputs();
    boolean isCollection();
  }

  interface StepExpression extends Serializable {
    boolean eval(FlowTaskExpressionContext context);
  }
  
  interface FlowTaskExpressionContext extends Function<String, Object> {
  }


  enum FlowTaskType {
    DT, SERVICE, USER_TASK, EMPTY, DECISION, EXCLUSIVE, MERGE, END
  }
}

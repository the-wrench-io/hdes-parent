package io.resys.hdes.compiler.spi;

/*-
 * #%L
 * hdes-compiler
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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;

public interface NamingContext {
  
  FlNamingContext fl();
  DtNamingContext dt();
  ClassName immutable(ClassName src);
  ClassName immutableBuilder(ClassName src);
  
  interface DtNamingContext {
    String pkg(DecisionTableBody body);
    ClassName interfaze(DecisionTableBody node);
    TypeName superinterface(DecisionTableBody node);
    ClassName impl(DecisionTableBody node);
    
    ClassName input(DecisionTableBody node);
    ClassName inputSuperinterface(DecisionTableBody node);
    
    ClassName output(DecisionTableBody node);
    ClassName outputSuperinterface(DecisionTableBody node);
  }
 
  interface FlNamingContext {
    String pkg(FlowBody body);
    
    ClassName ref(TaskRef ref);
    ClassName refInput(TaskRef ref);
    ClassName refOutput(TaskRef ref);
    String refMethod(TaskRef ref);
    
    ClassName interfaze(FlowBody node);
    TypeName superinterface(FlowBody node); 
    
    ClassName state(FlowBody node);
    TypeName stateSuperinterface(FlowBody node);
    
    ClassName impl(FlowBody node);
    ClassName input(FlowBody node);
    ClassName input(FlowBody node, ObjectTypeDefNode object);
    
    ClassName output(FlowBody node);
    
    ClassName taskState(FlowBody body, FlowTaskNode task);
    TypeName taskStateSuperinterface(FlowBody body, FlowTaskNode task);
  }
}

package io.resys.hdes.client.api.ast;

import java.io.Serializable;
import java.util.List;

/*-
 * #%L
 * wrench-assets-datatype
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.AstDataType.ValueType;

public interface AstType extends Serializable {
  
  String getName();
  @Nullable
  String getDescription();
  int getRev();
  
  List<AstCommandType> getCommands();
  AstHeaders getHeaders();
  
  
  @Value.Immutable
  interface AstHeaders extends Serializable {
    List<AstDataType> getInputs();
    List<AstDataType> getOutputs();
  }
  
  interface AstExpression {
    String getSrc();
    ValueType getType();
    List<String> getConstants();
    Object getValue(Object entity);
  }
}
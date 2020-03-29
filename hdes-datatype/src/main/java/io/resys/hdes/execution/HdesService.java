package io.resys.hdes.execution;

/*-
 * #%L
 * hdes-datatype
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeInput;

public interface HdesService {
  
  ExecutableQuery executable();
  HistoryQuery history();
  Tags tags();
  
  interface Tags {
    Map<String, Tag> getValues();
  }
  
  interface Tag {
    String getName();
    String getHash();
    Collection<Executable> get();
    
    Optional<Executable> st(String name);
    Optional<Executable> dt(String name);
    Optional<Executable> flow(String name);
  }
  
  interface HistoryQuery {
    HistoryQuery dt();
    HistoryQuery flow();
    HistoryQuery ft();
    HistoryQuery name(String name);
    HistoryQuery tag(String tag);
    HistoryQuery id(String id);
    Flowable<Executable> get();
  }
  
  interface ExecutableQuery {
    Executable dt(String name);
    Executable flow(String name);
    Executable st(String name);
    
    ExecutableQuery tag(String name);
    ExecutableQuery label(String label);
    Flowable<Executable> get();
  }
  
  interface Executable {
    String getId();
    String getName();
    String getLabel();
    String getTag();
    Collection<DataType> getTypes();
    <T extends ExecutionValue> Single<Execution<T>> run(String id, DataTypeInput input);
  }
  
  @Value.Immutable
  interface Execution<T extends ExecutionValue> extends Serializable {
    String getId();
    String getName();
    String getLabel();
    String getTag();
    LocalDateTime getLocalDateTime();
    List<DataType> getTypes();
    List<T> getValue();
  }
  
  @Value.Immutable
  interface ExecutionValue extends Serializable {
    Map<String, Serializable> getInputs();
    Map<String, Serializable> getOutputs();
  }
}

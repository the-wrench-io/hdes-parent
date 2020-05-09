package io.resys.hdes.compiler.api;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface Flow2 {
  
  FlowState apply(FlowStart input);
  FlowState apply(FlowSuspended state, FlowData fields);
  
  interface FlowData {}
  interface FlowState {}

  interface FlowStart extends FlowState {
    long getStart();
    FlowData getValue();
  }

  interface FlowEnd extends FlowState {
    long getStart();
    Optional<Long> getEnd();
    Optional<Long> getDuration();
    FlowState getParent();
    FlowData getValue();
  }
  
  interface FlowTask extends FlowState {
    long getStart();
    Optional<Long> getEnd();
    Optional<Long> getDuration();
    String getId();
    FlowState getParent();
    Optional<FlowTaskRef> getRef();
  }
  
  interface FlowTaskRef {
    String getId();
  }
  
  interface FlowTaskLoop extends FlowTaskRef {
    List<FlowState> getValues();
  }
  
  interface FlowTaskRefObject extends FlowTaskRef {
    FlowData getIn();
    Optional<FlowData> getOut();
  }
  
  interface FlowTaskRefArray extends FlowTaskRef {
    FlowData getIn();
    List<FlowData> getOut();
  }
  
  interface FlowSuspended {
    FlowState getParent();
  }
  
  interface FlowSerializer {
    Flow2 read(InputStream inputStream);
    void write(Flow2 flow, OutputStream output);
  }
}

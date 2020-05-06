package io.resys.hdes.servicetask.api;

/*-
 * #%L
 * hdes-servicetask
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

import io.resys.hdes.servicetask.api.ServiceTask.Input;
import io.resys.hdes.servicetask.api.ServiceTask.Output;

public interface ServiceTask<I extends Input, O extends Output, T> {
  
  O execute(I input, T context);
  Class<I> getInputType();
  Class<O> getOutputType();

  interface Input extends Serializable {
  }
  
  interface Output extends Serializable {
  }
}

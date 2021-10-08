package io.resys.hdes.client.api.execution;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.client.api.ast.AstService;

public interface ServiceProgram {
  AstService getModel();
  Object execute(List<Object> context, ServiceInit init);
  void stop();  
  
  @FunctionalInterface
  interface ServiceInit {
    <T> T get(Class<T> type);
  }
  interface ServiceExecutorType0<O> {
    O execute();
  }  
  interface ServiceExecutorType1<I, O> {
    O execute(I input1);
  }

  interface ServiceExecutorType2<I, I2, O> {
    O execute(I input1, I2 input2);
  }
}

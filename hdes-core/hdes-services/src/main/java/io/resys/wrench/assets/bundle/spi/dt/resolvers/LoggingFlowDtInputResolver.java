package io.resys.wrench.assets.bundle.spi.dt.resolvers;

/*-
 * #%L
 * wrench-assets-bundle
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
import java.util.Map;

import io.resys.hdes.client.api.HdesClient.ExecutorInput;
import io.resys.hdes.client.api.ast.TypeDef;

public class LoggingFlowDtInputResolver implements Serializable, ExecutorInput {
  private static final long serialVersionUID = 599120195165289167L;
  private final Map<String, Serializable> log;
  private final ExecutorInput delegate;

  public LoggingFlowDtInputResolver(Map<String, Serializable> log, ExecutorInput delegate) {
    super();
    this.log = log;
    this.delegate = delegate;
  }

  @Override
  public Object apply(TypeDef t) {
    Object result = delegate.apply(t);
    if(result == null || result instanceof Serializable) {
      log.put(t.getName(), (Serializable) result);
    } else {
      log.put(t.getName(), "can't serialize!");
    }
    return result;
  }
}

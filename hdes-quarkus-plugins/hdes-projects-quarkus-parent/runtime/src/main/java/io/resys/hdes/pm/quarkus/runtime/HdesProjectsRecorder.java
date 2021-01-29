package io.resys.hdes.pm.quarkus.runtime;

/*-
 * #%L
 * hdes-projects-quarkus
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

import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;
import io.resys.hdes.pm.quarkus.runtime.handlers.HdesProjectsUiStaticHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class HdesProjectsRecorder {
  
  public BeanContainerListener listener(String connectionUrl) {
    return beanContainer -> {      
      HdesProjectsContextProducer producer = beanContainer.instance(HdesProjectsContextProducer.class);
      producer.setLocal(connectionUrl);
    };
  }

  public Handler<RoutingContext> handler(String destination, String uiPath) {
    return new HdesProjectsUiStaticHandler(destination, uiPath);
  }
}

package io.resys.hdes.ui.quarkus.runtime;

/*-
 * #%L
 * Quarkus - Hdes UI - Runtime
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

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.http.runtime.ThreadLocalHandler;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

@Recorder
public class HdesBackendRecorder {
  
  public BeanContainerListener listener(Optional<String> local) {
    return beanContainer -> {      
      HdesBackendProducer producer = beanContainer.instance(HdesBackendProducer.class);
      producer.setLocal(local);
    };
  }

  public Handler<RoutingContext> handler(String destination, String uiPath) {
    Handler<RoutingContext> handler = new ThreadLocalHandler(() -> StaticHandler.create()
        .setAllowRootFileSystemAccess(true)
        .setWebRoot(destination)
        .setDefaultContentEncoding(StandardCharsets.UTF_8.name()));
    return (RoutingContext event) -> {
      if (event.normalisedPath().length() == uiPath.length()) {
        event.response()
            .setStatusCode(302)
            .headers().set(HttpHeaders.LOCATION, uiPath + "/");
        event.response().end();
      } else if (event.normalisedPath().length() == uiPath.length() + 1) {
        event.reroute(uiPath + "/index.html");
      } else {
        handler.handle(event);
      }
    };
  }
}

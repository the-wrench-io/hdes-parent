package io.resys.hdes.pm.quarkus.runtime;

import java.util.function.Function;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

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
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.resys.hdes.pm.quarkus.runtime.handlers.HdesReProjectsResourceHandler;
import io.resys.hdes.pm.quarkus.runtime.handlers.HdesResourceEditorUiStaticHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class HdesResourceEditorRecorder {

  public BeanContainerListener listener(String connectionUrl, String dbName) {
    return beanContainer -> {
      HdesResourceEditorContextProducer producer = beanContainer.instance(HdesResourceEditorContextProducer.class);
      producer
        .setDbName(dbName)
        .setConnectionUrl(connectionUrl);
    };
  }

  public Handler<RoutingContext> uiHandler(String destination, String uiPath) {
    return new HdesResourceEditorUiStaticHandler(destination, uiPath);
  }
  
  public Handler<RoutingContext> projectHandler() {
    Instance<CurrentIdentityAssociation> identityAssociations = CDI.current().select(CurrentIdentityAssociation.class);
    CurrentIdentityAssociation association;
    if (identityAssociations.isResolvable()) {
      association = identityAssociations.get();
    } else {
      association = null;
    }
    CurrentVertxRequest currentVertxRequest = CDI.current().select(CurrentVertxRequest.class).get();
    return new HdesReProjectsResourceHandler(association, currentVertxRequest);
  }
  
  public Function<Router, Route> routeFunction(String rootPath, Handler<RoutingContext> bodyHandler) {
    return new Function<Router, Route>() {
      @Override
      public Route apply(Router router) {
        return router.route(rootPath).handler(bodyHandler);
      }
    };
  }
}
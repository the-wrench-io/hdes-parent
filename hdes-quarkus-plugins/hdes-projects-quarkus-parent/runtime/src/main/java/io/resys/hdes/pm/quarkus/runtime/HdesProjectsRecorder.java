package io.resys.hdes.pm.quarkus.runtime;

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

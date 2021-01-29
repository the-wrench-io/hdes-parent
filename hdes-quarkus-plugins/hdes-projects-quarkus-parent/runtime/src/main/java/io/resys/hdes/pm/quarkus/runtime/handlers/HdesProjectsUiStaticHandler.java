package io.resys.hdes.pm.quarkus.runtime.handlers;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class HdesProjectsUiStaticHandler implements Handler<RoutingContext> {

  private String uiFinalDestination;
  private String uiPath;

  public HdesProjectsUiStaticHandler() {}

  public HdesProjectsUiStaticHandler(String uiFinalDestination, String uiPath) {
    this.uiFinalDestination = uiFinalDestination;
    this.uiPath = uiPath;
  }
  public String getUiFinalDestination() {
    return uiFinalDestination;
  }
  public void setUiFinalDestination(String uiFinalDestination) {
    this.uiFinalDestination = uiFinalDestination;
  }
  public String getUiPath() {
    return uiPath;
  }
  public void setUiPath(String uiPath) {
    this.uiPath = uiPath;
  }

  @Override
  public void handle(RoutingContext event) {
    StaticHandler staticHandler = StaticHandler.create()
      .setAllowRootFileSystemAccess(true)
      .setWebRoot(uiFinalDestination)
      .setDefaultContentEncoding("UTF-8");

    if (event.normalisedPath().length() == uiPath.length()) {
      event.response().setStatusCode(302);
      event.response().headers().set(HttpHeaders.LOCATION, uiPath + "/");
      event.response().end();
      return;
    } else if (event.normalisedPath().length() == uiPath.length() + 1) {
      event.reroute(uiPath + "/index.html");
      return;
    }
    staticHandler.handle(event);
  }
}
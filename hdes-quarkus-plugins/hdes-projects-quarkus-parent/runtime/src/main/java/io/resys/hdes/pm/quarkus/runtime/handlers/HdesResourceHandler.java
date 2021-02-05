package io.resys.hdes.pm.quarkus.runtime.handlers;
/*-
 * #%L
 * hdes-ui-quarkus
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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser;
import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.spi.support.ImmutableStatusMessage;
import io.resys.hdes.projects.spi.support.RepoAssert;
import io.resys.hdes.projects.spi.support.RepoAssert.StatusMessage;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public abstract class HdesResourceHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(HdesResourceHandler.class);
  private final CurrentIdentityAssociation currentIdentityAssociation;
  private final CurrentVertxRequest currentVertxRequest;
  
  public HdesResourceHandler(
      CurrentIdentityAssociation currentIdentityAssociation,
      CurrentVertxRequest currentVertxRequest) {
    super();
    this.currentIdentityAssociation = currentIdentityAssociation;
    this.currentVertxRequest = currentVertxRequest;
  }
  
  protected abstract void handleResource(RoutingContext event, HttpServerResponse response, HdesProjectsContext ctx);
  
  protected void handleSecurity(RoutingContext event) {
    if (currentIdentityAssociation != null) {
      QuarkusHttpUser existing = (QuarkusHttpUser) event.user();
      if (existing != null) {
        SecurityIdentity identity = existing.getSecurityIdentity();
        currentIdentityAssociation.setIdentity(identity);
      } else {
        currentIdentityAssociation.setIdentity(QuarkusHttpUser.getSecurityIdentity(event, null));
      }
    }
    currentVertxRequest.setCurrent(event);
  }
  
  @Override
  public void handle(RoutingContext event) {
    ManagedContext requestContext = Arc.container().requestContext();
    if (requestContext.isActive()) {
      handleSecurity(event);      
      HttpServerResponse response = event.response();
      HdesProjectsContext ctx = CDI.current().select(HdesProjectsContext.class).get();
      try {
        handleResource(event, response, ctx);
      } catch (Exception e) {
        catch422(e, ctx, response);
      }
     return; 
    }
    
    HttpServerResponse response = event.response();
    HdesProjectsContext ctx = CDI.current().select(HdesProjectsContext.class).get();
    try {
      requestContext.activate();
      handleSecurity(event);
      handleResource(event, response, ctx);
    } finally {
      requestContext.terminate();
    }
  }
  
  public static void catch404(String id, HdesProjectsContext ctx, HttpServerResponse response) {
    
    // Log error
    String log = new StringBuilder().append("Token not found with id: ").append(id).toString();
    String hash = RepoAssert.exceptionHash(log);
    LOGGER.error(hash + " - " + log);
    
    // Msg back to ui
    List<StatusMessage> messages = Arrays.asList(ImmutableStatusMessage.builder()
        .id("not-found")
        .value(log)
        .logCode(hash)
        .logStack(log)
        .build());
    response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
    response.setStatusCode(422);
    response.end(Buffer.buffer(ctx.writer().build(messages)));
  }
  
  public static void catch422(Exception e, HdesProjectsContext ctx, HttpServerResponse response) {
    String stack = ExceptionUtils.getStackTrace(e);
    
    // Log error
    String log = new StringBuilder().append(e.getMessage()).append(System.lineSeparator()).append(stack).toString();
    String hash = RepoAssert.exceptionHash(log);
    LOGGER.error(hash + " - " + log);
    
    // Msg back to ui
    List<StatusMessage> messages = Arrays.asList(ImmutableStatusMessage.builder()
        .id("error-msg")
        .value(e.getMessage() == null ? "not available": e.getMessage())
        .logCode(hash)
        .logStack(stack)
        .build());
    response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
    response.setStatusCode(422);
    response.end(Buffer.buffer(ctx.writer().build(messages)));
  }
}

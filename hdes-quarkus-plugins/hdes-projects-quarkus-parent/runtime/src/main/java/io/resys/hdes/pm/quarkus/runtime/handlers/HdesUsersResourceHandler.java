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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.api.ImmutableUser;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.commands.BatchCommands.BatchUser;
import io.resys.hdes.projects.api.commands.BatchCommands.UserResource;
import io.resys.hdes.projects.api.commands.ImmutableBatchUser;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;


public class HdesUsersResourceHandler implements Handler<RoutingContext>  {
  private static final Logger LOGGER = LoggerFactory.getLogger(HdesUsersResourceHandler.class);
  
  @Override
  public void handle(RoutingContext event) {
    boolean active = HandlerHelper.active();
    HttpServerResponse response = event.response();
    HdesProjectsContext ctx = CDI.current().select(HdesProjectsContext.class).get();
    
    try {
      switch (event.request().method()) {
      case GET:
        Collection<UserResource> defs = ctx.repo().batch().queryUsers().find();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(ctx.writer().build(defs)));    
        break;
        
      case DELETE:
        User toDelete = ctx.reader().build(event.getBody().getBytes(), ImmutableUser.class);
        User deleted = ctx.repo().users().delete().id(toDelete.getId()).rev(toDelete.getRev()).build();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(ctx.writer().build(deleted)));    
        break;
        
      case POST:
        BatchUser create = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchUser.class);
        UserResource created = ctx.repo().batch().createOrUpdateUser(create);
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(ctx.writer().build(created)));    
        break;
        
      case PUT:
        BatchUser update = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchUser.class);
        UserResource updated = ctx.repo().batch().createOrUpdateUser(update);
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(ctx.writer().build(updated)));    
        break;
      default:
        break;
      }
    } catch(Exception e) {
      HandlerHelper.catch422(e, ctx, response);
    } finally {
      if (active) {
        Arc.container().requestContext().terminate();
      }
    }
  }
  
  protected String exceptionHash(String msg) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.reset();
      md5.update(msg.getBytes(Charset.forName("UTF-8")));
      byte[] digest = md5.digest();
      return Hex.encodeHexString(digest);
    } catch (NoSuchAlgorithmException ex) {
      // Fall back to just hex timestamp in this improbable situation
      LOGGER.warn("MD5 Digester not found, falling back to timestamp hash", ex);
      long timestamp = System.currentTimeMillis();
      return Long.toHexString(timestamp);
    }
  }
}

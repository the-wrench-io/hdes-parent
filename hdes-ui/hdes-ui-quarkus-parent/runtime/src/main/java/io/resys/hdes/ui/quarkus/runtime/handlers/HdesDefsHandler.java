package io.resys.hdes.ui.quarkus.runtime.handlers;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import io.resys.hdes.backend.api.HdesBackend;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefChangeEntry;
import io.resys.hdes.backend.api.HdesBackend.DefCreateEntry;
import io.resys.hdes.backend.api.HdesBackend.DefDeleteEntry;
import io.resys.hdes.backend.api.HdesBackend.StatusMessage;
import io.resys.hdes.backend.api.ImmutableDefChangeEntry;
import io.resys.hdes.backend.api.ImmutableDefCreateEntry;
import io.resys.hdes.backend.api.ImmutableDefDeleteEntry;
import io.resys.hdes.backend.api.ImmutableStatusMessage;
import io.resys.hdes.ui.quarkus.runtime.HdesHandlerHelper;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;


public class HdesDefsHandler implements Handler<RoutingContext>  {
  private static final Logger LOGGER = LoggerFactory.getLogger(HdesDefsHandler.class);
  
  @Override
  public void handle(RoutingContext event) {
    boolean active = HdesHandlerHelper.active();
    HttpServerResponse response = event.response();
    HdesBackend backend = CDI.current().select(HdesBackend.class).get();
    
    try {
      switch (event.request().method()) {
      case GET:
        Collection<Def> defs = backend.query().find();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(defs)));    
        break;
        
      case DELETE:
        DefDeleteEntry toDelete = backend.reader().build(event.getBody().getBytes(), ImmutableDefDeleteEntry.class);
        List<Def> deleted = backend.delete().entry(toDelete).build();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(deleted)));
        break;
        
      case POST:
        List<DefCreateEntry> create = new ArrayList<>(backend.reader().list(event.getBody().getBytes(), ImmutableDefCreateEntry.class));
        List<Def> created = backend.builder().add(create).build();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(created)));
        break;
        
      case PUT:
        DefChangeEntry change = backend.reader().build(event.getBody().getBytes(), ImmutableDefChangeEntry.class);
        Def changed = backend.change().add(change).build().get(0);
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(changed)));
        break;
      default:
        break;
      }
    } catch(Exception e) {
      String stack = ExceptionUtils.getStackTrace(e);
      
      // Log error
      String log = new StringBuilder().append(e.getMessage()).append(System.lineSeparator()).append(stack).toString();
      String hash = exceptionHash(log);
      LOGGER.error(hash + " - " + log);
      
      // Msg back to ui
      List<StatusMessage> messages = Arrays.asList(ImmutableStatusMessage.builder()
          .id("error-msg")
          .value(e.getMessage())
          .logCode(hash)
          .logStack(stack)
          .build());
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.setStatusCode(422);
      response.end(Buffer.buffer(backend.writer().build(messages)));
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

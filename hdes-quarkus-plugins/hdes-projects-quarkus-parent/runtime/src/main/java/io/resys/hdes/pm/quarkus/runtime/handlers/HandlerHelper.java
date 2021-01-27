package io.resys.hdes.pm.quarkus.runtime.handlers;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.spi.support.ImmutableStatusMessage;
import io.resys.hdes.projects.spi.support.RepoAssert;
import io.resys.hdes.projects.spi.support.RepoAssert.StatusMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;

public class HandlerHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(HandlerHelper.class);
  
  public static boolean active() {
    ManagedContext context = Arc.container().requestContext();
    if (context.isActive()) {
      return false;
    }
    context.activate();
    return true;
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

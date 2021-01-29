package io.resys.hdes.app;

/*-
 * #%L
 * hdes-dev-app
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

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class ApplicationExceptionMapper implements ExceptionMapper<Exception> {
  

  @Override
  public Response toResponse(Exception e) {
    Response.Status httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
    return Response.status(httpStatus)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(e.getMessage())
        .build();
  }
}

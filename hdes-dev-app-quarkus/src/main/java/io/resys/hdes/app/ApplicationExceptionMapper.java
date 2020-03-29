package io.resys.hdes.app;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.resys.hdes.app.service.api.ApplicationService;

@Provider
@ApplicationScoped
public class ApplicationExceptionMapper implements ExceptionMapper<Exception> {
  
  private final ApplicationService service;
  
  public ApplicationExceptionMapper(ApplicationService service) {
    super();
    this.service = service;
  }

  @Override
  public Response toResponse(Exception e) {
    Response.Status httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
    return Response.status(httpStatus)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(service.exception().value(e).build())
        .build();
  }
}

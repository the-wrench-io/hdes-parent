package io.resys.hdes.app;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.api.ApplicationService.SaveRequest;

@Path("/hdes-dev-app")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource {
  
  private final ApplicationService applicationService;
  
  public ApplicationResource(ApplicationService applicationService) {
    super();
    this.applicationService = applicationService;
  }

  @GET
  @Path("/models")
  public Collection<ApplicationService.Model> models() {
    return applicationService.query().get();
  }
  
  @POST
  @Path("/changes")
  public Collection<ApplicationService.SaveResponse> save(SaveRequest[] requests) {
    return applicationService.save().add(requests).build();
  }
  
  @GET
  @Path("/health")
  public ApplicationService.Health health() {
    return applicationService.health().get();
  }
}
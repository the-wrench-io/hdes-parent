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

//@Path("/hdes-dev-app")
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
//public class ApplicationResource {
//  
//  private final ApplicationService applicationService;
//  
//  public ApplicationResource(ApplicationService applicationService) {
//    super();
//    this.applicationService = applicationService;
//  }
//
//  @GET
//  @Path("/models")
//  public Collection<ApplicationService.Model> models() {
//    return applicationService.query().get();
//  }
//  
//  @POST
//  @Path("/changes")
//  public Collection<ApplicationService.SaveResponse> save(SaveRequest[] requests) {
//    return applicationService.save().add(requests).build();
//  }
//  
//  @GET
//  @Path("/health")
//  public ApplicationService.Health health() {
//    return applicationService.health().get();
//  }
//}

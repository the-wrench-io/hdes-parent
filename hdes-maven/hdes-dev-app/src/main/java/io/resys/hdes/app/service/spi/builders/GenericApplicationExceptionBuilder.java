package io.resys.hdes.app.service.spi.builders;

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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.app.service.api.ApplicationService.ExceptionBuilder;
import io.resys.hdes.app.service.api.ApplicationService.Health;
import io.resys.hdes.app.service.api.ImmutableHealth;
import io.resys.hdes.app.service.api.ImmutableHealthValue;

public class GenericApplicationExceptionBuilder implements ExceptionBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenericApplicationExceptionBuilder.class);
  
  private Exception e;

  @Override
  public ExceptionBuilder value(Exception e) {
    this.e = e;
    return this;
  }

  @Override
  public Health build() {
    LOGGER.error(e.getMessage(), e);
    
    return ImmutableHealth.builder().status("ERROR").values(Arrays.asList(
        ImmutableHealthValue.builder().id(e.getClass().getSimpleName()).value(e.getMessage() == null ? "": e.getMessage()).build()
        )).build();
  }
}

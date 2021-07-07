package io.resys.wrench.assets.bundle.spi.tag;

/*-
 * #%L
 * wrench-assets-services
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceIdGen;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.builders.TemplateServiceBuilder;
import io.resys.wrench.assets.bundle.spi.exceptions.DataException;
import io.resys.wrench.assets.bundle.spi.exceptions.Message;
import io.resys.wrench.assets.bundle.spi.exceptions.MessageList;

public class TagServiceBuilder extends TemplateServiceBuilder {

  private final ServiceIdGen idGen;
  private final Optional<Pattern> pattern;
  private final String tagFormat;
  
  public TagServiceBuilder(
      String tagFormat,
      ServiceIdGen idGen,
      String defaultContent) {
    super();
    this.idGen = idGen;
    this.tagFormat = tagFormat;
    this.pattern = StringUtils.isEmpty(tagFormat) ? Optional.empty() : Optional.of(Pattern.compile(tagFormat));
    
  }

  @Override
  public Service build() {
    String serviceId = id == null ? idGen.nextId() : id;
    if(!ignoreErrors && pattern.isPresent() && !pattern.get().matcher(name).matches()) {
      throw new DataException(new MessageList().setStatus(422).add(new Message()
          .setCode("incorrectTag")
          .setValue("Incorrect tag format, expecting: " + tagFormat + "!")));
    }
    
    try {
      ServiceDataModel dataModel = new TagServiceDataModelBuilder().build(serviceId, name);

      return ImmutableServiceBuilder.newTag()
          .setId(serviceId)
          .setRev("")
          .setName(name)
          .setDescription(null)
          .setSrc(name + System.lineSeparator() + tagFormat)
          .setPointer("mem://" + name)
          .setModel(dataModel)
          .setExecution(() -> null)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Name: " + name + System.lineSeparator() + e.getMessage(), e);
    }
  }

  @Override
  public ServiceBuilder rename() {
    return this;
  }

}

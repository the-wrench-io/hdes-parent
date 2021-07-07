package io.resys.wrench.assets.bundle.spi.builders;

import java.util.function.Supplier;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ExportBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ExportType;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.api.model.DecisionTable;

public class GenericExportBuilder implements ExportBuilder {

  private final Supplier<DecisionTableExporter> exporter;
  private ExportType type;
  private Service service;

  public GenericExportBuilder(Supplier<DecisionTableExporter> exporter) {
    super();
    this.exporter = exporter;
  }

  @Override
  public ExportBuilder service(Service service) {
    this.service = service;
    return this;
  }

  @Override
  public ExportBuilder type(ExportType type) {
    this.type = type;
    return this;
  }

  @Override
  public String build() {
    Assert.notNull(service, "service can't be null!");
    Assert.isTrue(service.getType() == ServiceType.DT, "csv export is supported only for DT!");
    StringBuilder stringBuilder = new StringBuilder();

    service.newExecution().run(e -> stringBuilder.append(exporter.get()
        .format(DecisionTableFormat.valueOf(type.name()))
        .src((DecisionTable) e)
        .build()));

    return stringBuilder.toString();

  }


}

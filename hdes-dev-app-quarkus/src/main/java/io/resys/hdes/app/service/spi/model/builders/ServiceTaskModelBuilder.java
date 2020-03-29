package io.resys.hdes.app.service.spi.model.builders;

import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.spi.model.ModelFactory;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.app.service.spi.model.ModelFactory.ModelBuilder;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;

public class ServiceTaskModelBuilder implements ModelBuilder {
  private final ModelServices services;
  private Changes changes;

  public ServiceTaskModelBuilder(ModelServices services) {
    super();
    this.services = services;
  }

  @Override
  public ModelBuilder from(Changes changes) {
    this.changes = changes;
    return this;
  }

  @Override
  public Model build() {
    Assert.notNull(changes, () -> "changes must be defined!");

    return null;
  }
}

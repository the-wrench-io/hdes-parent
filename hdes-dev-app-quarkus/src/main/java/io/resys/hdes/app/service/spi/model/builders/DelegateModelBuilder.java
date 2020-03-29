package io.resys.hdes.app.service.spi.model.builders;

import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.spi.model.ModelBuilderException;
import io.resys.hdes.app.service.spi.model.ModelBuilderNotDefinedException;
import io.resys.hdes.app.service.spi.model.ModelFactory.ModelBuilder;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;


public class DelegateModelBuilder implements ModelBuilder {
  private final ModelServices services;
  private Changes changes;

  public DelegateModelBuilder(ModelServices services) {
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
    
    ModelBuilder delegate;
    switch (changes.getLabel()) {
    case "dt":
      delegate = new DecisionTableModelBuilder(services);
      break;
    case "st":
      delegate = new ServiceTaskModelBuilder(services);
      break;
    case "fl":
      delegate = new FlowModelBuilder(services);
      break;
    case "tg":
      delegate = new TagModelBuilder(services);
      break;
    default:
      throw ModelBuilderNotDefinedException.builder().changes(changes).build();
    }
    
    try {
      return delegate.from(changes).build();
    } catch(Exception e) {
      throw ModelBuilderException.builder().changes(changes).exception(e).build();
    }
  }
}

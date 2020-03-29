package io.resys.hdes.app.service.spi.model;

import io.resys.hdes.app.service.spi.model.builders.DelegateModelBuilder;

public class GenericModelFactory implements ModelFactory {

  private final ModelServices services;
  
  public GenericModelFactory(ModelServices services) {
    super();
    this.services = services;
  }

  @Override
  public ModelBuilder create() {
    return new DelegateModelBuilder(services);
  }

  @Override
  public ModelChangeBuilder change() {
    // TODO Auto-generated method stub
    return null;
  }
}

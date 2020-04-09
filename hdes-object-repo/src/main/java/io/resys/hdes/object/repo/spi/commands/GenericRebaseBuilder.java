package io.resys.hdes.object.repo.spi.commands;

import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.RebaseBuilder;

public class GenericRebaseBuilder implements RebaseBuilder {
  private final Objects objects;
  private String name;

  public GenericRebaseBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public RebaseBuilder ref(String refName) {
    this.name = refName;
    return this;
  }

  @Override
  public Objects build() {
    Status status = new GenericStatusBuilder(objects).ref(name).build().getEntries();
    return null;
  }
}

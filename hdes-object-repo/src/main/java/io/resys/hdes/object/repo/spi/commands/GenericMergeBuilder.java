package io.resys.hdes.object.repo.spi.commands;

import io.resys.hdes.object.repo.api.ObjectRepository.MergeBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;

public class GenericMergeBuilder implements MergeBuilder {
  private final Objects objects;
  private String head;

  public GenericMergeBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public MergeBuilder head(String name) {
    this.head = name;
    return this;
  }

  @Override
  public Objects build() {
    // TODO Auto-generated method stub
    return null;
  }
}

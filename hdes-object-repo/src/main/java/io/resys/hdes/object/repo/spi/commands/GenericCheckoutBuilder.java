package io.resys.hdes.object.repo.spi.commands;

import java.util.Arrays;
import java.util.List;

import io.resys.hdes.object.repo.api.ImmutableHead;
import io.resys.hdes.object.repo.api.ObjectRepository.CheckoutBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Snapshot;

public abstract class GenericCheckoutBuilder implements CheckoutBuilder {

  private final Objects objects;
  private String name;

  public GenericCheckoutBuilder(Objects objects) {
    super();
    this.objects = objects;
  }
  
  @Override
  public CheckoutBuilder from(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Objects build() {
    Snapshot snapshot = new GenericSnapshotBuilder(objects).from(name).build();
    Head head = ImmutableHead.builder().value(name).snapshot(snapshot).build();
    return save(Arrays.asList(head));
  }
  
  protected abstract Objects save(List<Object> newObjects);
}

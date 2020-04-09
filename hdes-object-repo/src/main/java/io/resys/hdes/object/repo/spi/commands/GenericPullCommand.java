package io.resys.hdes.object.repo.spi.commands;

import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.PullCommand;

public abstract class GenericPullCommand implements PullCommand {

  private final Objects objects;
  
  public GenericPullCommand(Objects objects) {
    super();
    this.objects = objects;
  }
  
  @Override
  public Objects build() {
    Objects newState = fetch();  
    return newState;
  }
  
  protected abstract Objects fetch();
}

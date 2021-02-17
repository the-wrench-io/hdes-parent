package io.resys.hdes.assetdb.spi.commands;

import io.resys.hdes.assetdb.api.AssetClient.Objects;
import io.resys.hdes.assetdb.api.AssetClient.PullCommand;

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

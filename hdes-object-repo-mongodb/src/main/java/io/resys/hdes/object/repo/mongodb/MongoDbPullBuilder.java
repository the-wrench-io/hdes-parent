package io.resys.hdes.object.repo.mongodb;

import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.PullBuilder;
import io.resys.hdes.object.repo.mongodb.MongoCommand.MongoDbConfig;

public class MongoDbPullBuilder implements PullBuilder {
  private final MongoCommand<Objects> command;
  private final MongoDbConfig mongoDbConfig;
  
  @Override
  public Objects build() {

    return null;
  }
}

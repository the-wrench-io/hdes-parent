package io.resys.hdes.projects.spi.mongodb;

/*-
 * #%L
 * hdes-pm-backend
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.projects.api.PmRepository;
import io.resys.hdes.projects.api.commands.AccessCommands;
import io.resys.hdes.projects.api.commands.BatchCommands;
import io.resys.hdes.projects.api.commands.GroupCommands;
import io.resys.hdes.projects.api.commands.ProjectCommands;
import io.resys.hdes.projects.api.commands.UserCommands;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.commands.MongoAccessCommands;
import io.resys.hdes.projects.spi.mongodb.commands.MongoBatchCommands;
import io.resys.hdes.projects.spi.mongodb.commands.MongoGroupCommands;
import io.resys.hdes.projects.spi.mongodb.commands.MongoGroupUserCommands;
import io.resys.hdes.projects.spi.mongodb.commands.MongoPersistentCommand;
import io.resys.hdes.projects.spi.mongodb.commands.MongoPersistentCommand.MongoTransaction;
import io.resys.hdes.projects.spi.mongodb.commands.MongoProjectCommands;
import io.resys.hdes.projects.spi.mongodb.commands.MongoUserCommands;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoPmRepository implements PmRepository {

  private final BatchCommands batchProjectCommands;
  private final ProjectCommands projectCommands;
  private final UserCommands userCommands;
  private final AccessCommands accessCommands;
  private final GroupCommands groupCommands;

  public MongoPmRepository(
      BatchCommands batchProjectCommands, 
      ProjectCommands projectCommands, 
      UserCommands userCommands,
      AccessCommands accessCommands,
      GroupCommands groupCommands) {
    super();
    this.batchProjectCommands = batchProjectCommands;
    this.projectCommands = projectCommands;
    this.userCommands = userCommands;
    this.accessCommands = accessCommands;
    this.groupCommands = groupCommands;
  }
  @Override
  public ProjectCommands projects() {
    return projectCommands;
  }
  @Override
  public UserCommands users() {
    return userCommands;
  }
  @Override
  public AccessCommands access() {
    return accessCommands;
  }
  @Override
  public BatchCommands batch() {
    return batchProjectCommands;
  }
  @Override
  public GroupCommands groups() {
    return groupCommands;
  }

  public static Builder builder() {
    return new Builder();
  } 
  
  public static class Builder {
    private MongoTransaction transaction;
    private MongoDbConfig config;

    public Builder transaction(MongoTransaction transaction) {
      this.transaction = transaction;
      return this;
    }

    public Builder config(MongoDbConfig config) {
      this.config = config;
      return this;
    }

    public MongoPmRepository build() {
      RepoAssert.notNull(transaction, () -> "transaction not defined!");
      if (config == null) {
        config = ImmutableMongoDbConfig.builder()
            .db("PM")
            .projects("projects")
            .users("users")
            .access("access")
            .groups("groups")
            .groupUsers("groupUsers")
            .build();
      }
      
      final var persistentCommand = new MongoPersistentCommand(transaction, config);
      final var projectCommands = new MongoProjectCommands(persistentCommand);
      final var userCommands = new MongoUserCommands(persistentCommand);
      final var groupCommands = new MongoGroupCommands(persistentCommand);
      final var groupUserCommands = new MongoGroupUserCommands(persistentCommand, groupCommands, userCommands);
      final var accessCommands = new MongoAccessCommands(persistentCommand, projectCommands, userCommands, groupCommands);
      final var batchProjectCommands = new MongoBatchCommands(projectCommands, userCommands, accessCommands, groupCommands, groupUserCommands);
      
      return new MongoPmRepository(batchProjectCommands, projectCommands, userCommands, accessCommands, groupCommands);
    }
  }
}

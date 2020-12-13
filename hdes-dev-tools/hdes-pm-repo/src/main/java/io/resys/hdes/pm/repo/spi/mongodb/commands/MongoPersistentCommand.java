package io.resys.hdes.pm.repo.spi.mongodb.commands;

/*-
 * #%L
 * hdes-pm-repo
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;

import io.resys.hdes.pm.repo.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand;
import io.resys.hdes.pm.repo.spi.mongodb.visitors.CreateEntityVisitor;
import io.resys.hdes.pm.repo.spi.mongodb.visitors.DeleteEntityVisitor;
import io.resys.hdes.pm.repo.spi.mongodb.visitors.UpdateEntityVisitor;

public class MongoPersistentCommand implements PersistentCommand {

  private final MongoTransaction transaction;
  private final MongoDbConfig config;

  public MongoPersistentCommand(MongoTransaction transaction, MongoDbConfig config) {
    super();
    this.transaction = transaction;
    this.config = config;
  }
  
  @FunctionalInterface
  public interface MongoTransaction {
    void accept(Consumer<MongoClient> client);
  }

  @Value.Immutable
  public interface MutableResult<T> {
    T getValue();
  }
  
  @Override
  public PersistedEntities create(Consumer<EntityVisitor> consumer) {
    final var collect = ImmutablePersistedEntities.builder();
    transaction.accept(client -> consumer.accept(CreateEntityVisitor.builder()
        .client(client)
        .config(config)
        .collect(collect)
        .build())
    );
    return collect.build();
  }

  @Override
  public PersistedEntities delete(Consumer<EntityVisitor> consumer) {
    final var collect = ImmutablePersistedEntities.builder();
    transaction.accept(client -> consumer.accept(DeleteEntityVisitor.builder()
        .client(client)
        .config(config)
        .collect(collect)
        .build())
    );
    return collect.build();
  }

  @Override
  public PersistedEntities update(Consumer<EntityVisitor> consumer) {
    final var collect = ImmutablePersistedEntities.builder();
    transaction.accept(client -> consumer.accept(UpdateEntityVisitor.builder()
        .client(client)
        .config(config)
        .collect(collect)
        .build())
    );
    return collect.build();
  }

  @Override
  public <T> T map(BiFunction<MongoClient, MongoDbConfig, T> consumer) {
    ImmutableMutableResult.Builder<T> builder = ImmutableMutableResult.builder();
    transaction.accept(client -> {
      T result = consumer.apply(client, config);
      builder.value(result);
    }); 
    return builder.build().getValue();
  }
}

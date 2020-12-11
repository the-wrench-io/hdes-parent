package io.resys.hdes.backend.spi.mongodb.commands;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;

import io.resys.hdes.backend.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.backend.spi.mongodb.PersistentCommand;
import io.resys.hdes.backend.spi.mongodb.visitors.CreateEntityVisitor;
import io.resys.hdes.backend.spi.mongodb.visitors.DeleteEntityVisitor;
import io.resys.hdes.backend.spi.mongodb.visitors.UpdateEntityVisitor;

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

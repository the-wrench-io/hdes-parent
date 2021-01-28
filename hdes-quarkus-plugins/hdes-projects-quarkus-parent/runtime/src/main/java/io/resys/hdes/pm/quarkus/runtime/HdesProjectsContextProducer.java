package io.resys.hdes.pm.quarkus.runtime;

import java.util.Arrays;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.internal.ProvidersCodecRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.quarkus.arc.DefaultBean;
import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.pm.quarkus.runtime.context.ImmutableHdesProjectsContext;
import io.resys.hdes.projects.spi.mongodb.MongoPmRepository;
import io.resys.hdes.projects.spi.mongodb.codecs.PMCodecProvider;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper.MongoTransaction;

@ApplicationScoped
public class HdesProjectsContextProducer {
  
  private String connectionUrl;
  
  public HdesProjectsContextProducer setLocal(String connectionUrl) {
    this.connectionUrl = connectionUrl;
    return this;
  }
  
  private static class MongoTransactionDefault implements MongoTransaction {
    private final MongoClient client;
    public MongoTransactionDefault(MongoClient client) {
      super();
      this.client = client;
    }
    @Override
    public <T> T accept(Function<MongoClient, T> action) {
      return action.apply(client);
    }
    
  }

  @Produces
  @Singleton
  @DefaultBean
  public HdesProjectsContext hdesProjectsBackend() {
    CodecRegistry codecRegistry = new ProvidersCodecRegistry(Arrays.asList(
      new PMCodecProvider(),
      new DocumentCodecProvider(),
      new Jsr310CodecProvider(),
      new ValueCodecProvider()
    ));
    
    MongoClient client = MongoClients.create(
        MongoClientSettings.builder()
        .codecRegistry(codecRegistry)
        .applyToConnectionPoolSettings(builder -> builder.build())
        .applyToClusterSettings(builder -> builder
            .hosts(Arrays.asList(new ServerAddress(connectionUrl)))
            .build() )
        .build());

    ObjectMapper objectMapper = new ObjectMapper();
    MongoTransaction transaction = new MongoTransactionDefault(client);    
    return new ImmutableHdesProjectsContext(
        objectMapper,
        MongoPmRepository.config().transaction(transaction).build()
    );
  }
  
}
package io.resys.hdes.pm.quarkus.runtime;

/*-
 * #%L
 * hdes-projects-quarkus
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
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.quarkus.arc.DefaultBean;
import io.resys.hdes.pm.quarkus.runtime.context.HdesResourceEditorContext;
import io.resys.hdes.pm.quarkus.runtime.context.ImmutableHdesResourceEditorContext;
import io.resys.hdes.resource.editor.spi.MongoReRepository;
import io.resys.hdes.resource.editor.spi.support.MongoWrapper.MongoTransaction;

@ApplicationScoped
public class HdesResourceEditorContextProducer {
  
  private String connectionUrl;
  private String dbName;
  private String adminInitUserName;
  

  public HdesResourceEditorContextProducer setInitAdminUserName(String adminInitUserName) {
    this.adminInitUserName = adminInitUserName;
    return this;
  }
  public HdesResourceEditorContextProducer setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
    return this;
  }
  public HdesResourceEditorContextProducer setDbName(String dbName) {
    this.dbName = dbName;
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
  public HdesResourceEditorContext hdesResourceEditorBackend() {
    CodecRegistry codecRegistry = new ProvidersCodecRegistry(Arrays.asList(
//      new PMCodecProvider(),
      new DocumentCodecProvider(),
      new Jsr310CodecProvider(),
      new ValueCodecProvider()
    ));

    MongoClient client = MongoClients.create(
        MongoClientSettings.builder()
        .codecRegistry(codecRegistry)
        .applyConnectionString(new ConnectionString(connectionUrl))
        .build());

    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule());
    
    MongoTransaction transaction = new MongoTransactionDefault(client);    
    return new ImmutableHdesResourceEditorContext(
        objectMapper,
        MongoReRepository.config()
          .dbName(dbName)
          .transaction(transaction)
          .build()
    );
  }
  
}

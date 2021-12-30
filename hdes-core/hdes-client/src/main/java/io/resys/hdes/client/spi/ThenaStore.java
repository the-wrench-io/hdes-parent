package io.resys.hdes.client.spi;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/*-
 * #%L
 * hdes-client-api
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

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ImmutableStoreExceptionMsg;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.exceptions.StoreException;
import io.resys.hdes.client.spi.ThenaConfig.EntityState;
import io.resys.hdes.client.spi.thena.DocumentQueryBuilder;
import io.resys.hdes.client.spi.thena.PersistenceCommands;
import io.resys.hdes.client.spi.thena.ZoeDeserializer;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.resys.thena.docdb.api.DocDB;
import io.resys.thena.docdb.api.actions.RepoActions.RepoStatus;
import io.resys.thena.docdb.spi.pgsql.DocDBFactory;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;

public class ThenaStore extends PersistenceCommands implements HdesStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThenaStore.class);

  public ThenaStore(ThenaConfig config) {
    super(config);
  }
  @Override
  public String getRepoName() {
    return config.getRepoName();
  }
  @Override
  public String getHeadName() {
    return config.getHeadName();
  }
  @Override
  public StoreRepoBuilder repo() {
    return new StoreRepoBuilder() {
      private String repoName;
      private String headName;
      @Override
      public StoreRepoBuilder repoName(String repoName) {
        this.repoName = repoName;
        return this;
      }
      @Override
      public StoreRepoBuilder headName(String headName) {
        this.headName = headName;
        return this;
      }
      @Override
      public Uni<HdesStore> create() {
        HdesAssert.notNull(repoName, () -> "repoName must be defined!");
        final var client = config.getClient();
        final var newRepo = client.repo().create().name(repoName).build();
        return newRepo.onItem().transform((repoResult) -> {
          if(repoResult.getStatus() != RepoStatus.OK) {
            throw new StoreException("REPO_CREATE_FAIL", null, 
                ImmutableStoreExceptionMsg.builder()
                .id(repoResult.getStatus().toString())
                .value(repoName)
                .addAllArgs(repoResult.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
                .build()); 
          }
          
          return build();
        });
      }
      @Override
      public HdesStore build() {
        HdesAssert.notNull(repoName, () -> "repoName must be defined!");
        return new ThenaStore(ImmutableThenaConfig.builder()
            .from(config)
            .repoName(repoName)
            .headName(headName == null ? config.getHeadName() : headName)
            .build());
      }
      @Override
      public Uni<Boolean> createIfNot() {
        final var client = config.getClient();
        
        return client.repo().query().id(config.getRepoName()).get().onItem().transformToUni(repo -> {
          if(repo == null) {
            return client.repo().create().name(config.getRepoName()).build().onItem().transform(newRepo -> true); 
          }
          return Uni.createFrom().item(true);
        });
      }
    };
  }
  
  @Override
  public QueryBuilder query() {
    return new DocumentQueryBuilder(config);
  }
  @Override
  public Uni<StoreEntity> create(CreateStoreEntity newType) {
    final var gid = gid(newType.getBodyType());
    final var entity = (StoreEntity) ImmutableStoreEntity.builder()
        .id(gid)
        .hash("")
        .body(newType.getBody())
        .bodyType(newType.getBodyType())
        .build();
    return super.save(entity);
  }

  @Override
  public Uni<StoreEntity> update(UpdateStoreEntity updateType) {
    final Uni<EntityState> query = getEntityState(updateType.getId());
    return query.onItem().transformToUni(state -> {
      final StoreEntity entity = ImmutableStoreEntity.builder()
          .from(state.getEntity())
          .id(updateType.getId())
          .bodyType(state.getEntity().getBodyType())
          .body(updateType.getBody())
          .build();
      return super.save(entity);
    });
  }

  @Override
  public Uni<StoreEntity> delete(DeleteAstType deleteType) {
    final Uni<EntityState> query = getEntityState(deleteType.getId());
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  private String gid(AstBodyType type) {
    return config.getGidProvider().getNextId(type);
  }

  @Override
  public HistoryQuery history() {
    // TODO Auto-generated method stub
    return null;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String repoName;
    private String headName;
    private ObjectMapper objectMapper;
    private ThenaConfig.GidProvider gidProvider;
    private ThenaConfig.AuthorProvider authorProvider;
    private io.vertx.mutiny.pgclient.PgPool pgPool;
    private String pgHost;
    private String pgDb;
    private Integer pgPort;
    private String pgUser;
    private String pgPass;
    private Integer pgPoolSize;
    
    public Builder repoName(String repoName) {
      this.repoName = repoName;
      return this;
    }
    public Builder objectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }
    public Builder gidProvider(ThenaConfig.GidProvider gidProvider) {
      this.gidProvider = gidProvider;
      return this;
    }
    public Builder authorProvider(ThenaConfig.AuthorProvider authorProvider) {
      this.authorProvider = authorProvider;
      return this;
    }
    public Builder pgPool(io.vertx.mutiny.pgclient.PgPool pgPool) {
      this.pgPool = pgPool;
      return this;
    }
    public Builder headName(String headName) {
      this.headName = headName;
      return this;
    }
    public Builder pgHost(String pgHost) {
      this.pgHost = pgHost;
      return this;
    }
    public Builder pgDb(String pgDb) {
      this.pgDb = pgDb;
      return this;
    }
    public Builder pgPort(Integer pgPort) {
      this.pgPort = pgPort;
      return this;
    }
    public Builder pgUser(String pgUser) {
      this.pgUser = pgUser;
      return this;
    }
    public Builder pgPass(String pgPass) {
      this.pgPass = pgPass;
      return this;
    }
    public Builder pgPoolSize(Integer pgPoolSize) {
      this.pgPoolSize = pgPoolSize;
      return this;
    }
    
    
    private ThenaConfig.GidProvider getGidProvider() {
      return this.gidProvider == null ? type -> {
        return UUID.randomUUID().toString();
     } : this.gidProvider;
    }
    
    private ThenaConfig.AuthorProvider getAuthorProvider() {
      return this.authorProvider == null ? ()-> "not-configured" : this.authorProvider;
    } 
    
    private ObjectMapper getObjectMapper() {
      if(this.objectMapper == null) {
        return this.objectMapper;
      }
      
      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new GuavaModule());
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.registerModule(new Jdk8Module());
      return objectMapper;
    }
    
    public ThenaStore build() {
      HdesAssert.notNull(repoName, () -> "repoName must be defined!");
    
      final var headName = this.headName == null ? "main": this.headName;
      if(LOGGER.isDebugEnabled()) {
        final var log = new StringBuilder()
          .append(System.lineSeparator())
          .append("Configuring Thena: ").append(System.lineSeparator())
          .append("  repoName: '").append(this.repoName).append("'").append(System.lineSeparator())
          .append("  headName: '").append(headName).append("'").append(System.lineSeparator())
          .append("  objectMapper: '").append(this.objectMapper == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          .append("  gidProvider: '").append(this.gidProvider == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          .append("  authorProvider: '").append(this.authorProvider == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          
          .append("  pgPool: '").append(this.pgPool == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          .append("  pgPoolSize: '").append(this.pgPoolSize).append("'").append(System.lineSeparator())
          .append("  pgHost: '").append(this.pgHost).append("'").append(System.lineSeparator())
          .append("  pgPort: '").append(this.pgPort).append("'").append(System.lineSeparator())
          .append("  pgDb: '").append(this.pgDb).append("'").append(System.lineSeparator())
          .append("  pgUser: '").append(this.pgUser == null ? "null" : "***").append("'").append(System.lineSeparator())
          .append("  pgPass: '").append(this.pgPass == null ? "null" : "***").append("'").append(System.lineSeparator());
          
        LOGGER.debug(log.toString());
      }
      
      final DocDB thena;
      if(pgPool == null) {
        HdesAssert.notNull(pgHost, () -> "pgHost must be defined!");
        HdesAssert.notNull(pgPort, () -> "pgPort must be defined!");
        HdesAssert.notNull(pgDb, () -> "pgDb must be defined!");
        HdesAssert.notNull(pgUser, () -> "pgUser must be defined!");
        HdesAssert.notNull(pgPass, () -> "pgPass must be defined!");
        HdesAssert.notNull(pgPoolSize, () -> "pgPoolSize must be defined!");
        
        final PgConnectOptions connectOptions = new PgConnectOptions()
            .setHost(pgHost)
            .setPort(pgPort)
            .setDatabase(pgDb)
            .setUser(pgUser)
            .setPassword(pgPass);
        final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(pgPoolSize);
        
        final io.vertx.mutiny.pgclient.PgPool pgPool = io.vertx.mutiny.pgclient.PgPool.pool(connectOptions, poolOptions);
        
        thena = DocDBFactory.create().client(pgPool).db(repoName).build();
      } else {
        thena = DocDBFactory.create().client(pgPool).db(repoName).build();
      }
      
      final ObjectMapper objectMapper = getObjectMapper();
      final ThenaConfig config = ImmutableThenaConfig.builder()
          .client(thena).repoName(repoName).headName(headName)
          .gidProvider(getGidProvider())
          .serializer((entity) -> {
            try {
              return objectMapper.writeValueAsString(ImmutableStoreEntity.builder().from(entity).hash("").build());
            } catch (IOException e) {
              throw new RuntimeException(e.getMessage(), e);
            }
          })
          .deserializer(new ZoeDeserializer(objectMapper))
          .authorProvider(getAuthorProvider())
          .build();
      return new ThenaStore(config);
    }
  }
}

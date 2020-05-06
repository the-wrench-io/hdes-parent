package io.resys.hdes.storage.mongodb;

/*-
 * #%L
 * hdes-storage-mongodb
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

import com.mongodb.client.MongoClient;

import io.reactivex.Single;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.StorageService;
import io.resys.hdes.storage.mongodb.builders.ChangesQueryBuilderMongoDb;
import io.resys.hdes.storage.mongodb.builders.ChangesSaveBuilderMongoDb;
import io.resys.hdes.storage.mongodb.config.MongoDbConfig;

public class StorageServiceMongoDb implements StorageService {
  private final ChangesOperations changesOperations;
  private final TagOperations tagOperations;
  
  public StorageServiceMongoDb(ChangesOperations changesOperations, TagOperations tagOperations) {
    super();
    this.changesOperations = changesOperations;
    this.tagOperations = tagOperations;
  }
  @Override
  public ChangesOperations changes() {
    return changesOperations;
  }
  @Override
  public TagOperations tag() {
    return tagOperations;
  }
  public static Config config() {
    return new Config();
  }

  private static class ChangesOperationsMongoDb implements ChangesOperations {
    private final Single<MongoClient> client;
    private final MongoDbConfig mongoDbConfig;
    private final TenantSupplier tenantSupplier;
    private final AuthorSupplier authorSupplier;
    private final TagSupplier tagSupplier;
    private final TagOperations tagOperations;
    
    public ChangesOperationsMongoDb(
        Single<MongoClient> client, 
        MongoDbConfig mongoDbConfig, 
        TenantSupplier tenantSupplier,
        AuthorSupplier authorSupplier,
        TagSupplier tagSupplier,
        TagOperations tagOperations) {
      super();
      this.client = client;
      this.mongoDbConfig = mongoDbConfig;
      this.tenantSupplier = tenantSupplier;
      this.authorSupplier = authorSupplier;
      this.tagSupplier = tagSupplier;
      this.tagOperations = tagOperations;
    }
    @Override
    public SaveBuilder save() {
      return new ChangesSaveBuilderMongoDb(
          mongoDbConfig, client,
          tenantSupplier, authorSupplier);
    }
    @Override
    public QueryBuilder query() {
      return new ChangesQueryBuilderMongoDb(
          mongoDbConfig, client,
          tenantSupplier, tagSupplier, tagOperations);
    }
  }

  public static class Config {
    private Single<MongoClient> client;

    public Config client(Single<MongoClient> client) {
      this.client = client;
      return this;
    }

    public StorageServiceMongoDb build() {
      Assert.notNull(client, () -> "client must be defined!");
      
      MongoDbConfig mongoDbConfig = new MongoDbConfig().setCollection("assets");
      TenantSupplier tenantSupplier = new DefaultTenantSupplier();
      AuthorSupplier authorSupplier = new DefaultAuthorSupplier();
      TagSupplier tagSupplier = () -> null;
      
      TagOperations tagOperations = null;
      ChangesOperations changes = new ChangesOperationsMongoDb(client, mongoDbConfig, tenantSupplier, authorSupplier, tagSupplier, tagOperations);
      return new StorageServiceMongoDb(changes, tagOperations);
    }
  }
}

package io.resys.hdes.storage.mongodb.builders;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ChangesRevisionException;
import io.resys.hdes.storage.api.ImmutableChanges;
import io.resys.hdes.storage.api.StorageService.AuthorSupplier;
import io.resys.hdes.storage.api.StorageService.SaveBuilder;
import io.resys.hdes.storage.api.StorageService.TenantSupplier;
import io.resys.hdes.storage.mongodb.codecs.ChangesCodec;
import io.resys.hdes.storage.mongodb.config.MongoDbConfig;

public class ChangesSaveBuilderMongoDb implements SaveBuilder {
  private final MongoDbConfig mongoDbConfig;
  private final Single<MongoClient> client;
  private final TenantSupplier tenantSupplier;
  private final AuthorSupplier authorSupplier;
  private final static PersistentStateCommand PERSIST = new PersistentStateCommand();
  private final static CopyStateCommand COPY = new CopyStateCommand();
  private Collection<DataTypeCommand> newChanges;
  private String id;
  private Integer revision;
  private String label;
  private String author;
  private String tenant;

  public ChangesSaveBuilderMongoDb(
      MongoDbConfig mongoDbConfig,
      Single<MongoClient> client,
      TenantSupplier tenantSupplier,
      AuthorSupplier authorSupplier) {
    super();
    this.client = client;
    this.mongoDbConfig = mongoDbConfig;
    this.tenantSupplier = tenantSupplier;
    this.authorSupplier = authorSupplier;
  }

  @Override
  public SaveBuilder id(String id) {
    this.id = id;
    return this;
  }

  @Override
  public SaveBuilder revision(int revision) {
    this.revision = revision;
    return this;
  }

  @Override
  public SaveBuilder changes(Collection<DataTypeCommand> changes) {
    this.newChanges = changes;
    return this;
  }

  @Override
  public SaveBuilder label(String label) {
    this.label = label;
    return this;
  }

  @Override
  public SaveBuilder author(String author) {
    this.author = author;
    return this;
  }

  @Override
  public SaveBuilder tenant(String tenant) {
    this.tenant = tenant;
    return this;
  }

  @Override
  public Single<Changes> build() {
    return accept(PERSIST);
  }
  
  @Override
  public Single<Changes> copy() {
    return accept(COPY);
  }
  
  private Single<Changes> accept(StateCommand command) {
    Assert.notNull(newChanges, () -> "changes can't be null!");
    final String tenant = this.tenant != null ? this.tenant : tenantSupplier.get();
    final String author = this.author != null ? this.author : authorSupplier.get().getId();
    return client.map(client -> {
      
      final MongoCollection<Changes> mongoCollection = client.getDatabase(tenant).getCollection(mongoDbConfig.getCollection(), Changes.class);
      final List<DataTypeCommand> commands = new ArrayList<>(this.newChanges);
      
      if (id != null) {
        Assert.notNull(revision, () -> "revision must be defined when updating changes");
        Bson filter = Filters.eq(ChangesCodec.CHANGES_ID, id);
        Changes changes = mongoCollection.find(filter).first();
        if(changes != null) {
          if(changes.getValues().size() != revision) {
            throw ChangesRevisionException.builder().changes(changes).revision(revision).build();
          }
          return command.update(changes, commands, mongoCollection, filter);
        }
      }
      
      String id = this.id == null ? UUID.randomUUID().toString() : this.id;
      Assert.notNull(label, () -> "label can't be null!");
      Changes changes = ImmutableChanges.builder()
          .id(id)
          .tenant(tenant)
          .label(label)
          .values(commands)
          .build();
      return command.create(changes, mongoCollection);
    });
  }
  
  private interface StateCommand {
    Changes create(Changes currentState, MongoCollection<Changes> mongoCollection);
    Changes update(Changes currentState, List<DataTypeCommand> stateChanges, MongoCollection<Changes> mongoCollection, Bson filter);
  }
  
  private static class PersistentStateCommand implements StateCommand {
    @Override
    public Changes update(Changes currentState, List<DataTypeCommand> stateChanges, MongoCollection<Changes> mongoCollection, Bson filter) {
      mongoCollection.updateOne(filter, Updates.pushEach(ChangesCodec.CHANGES_VALUES, stateChanges));
      return mongoCollection.find(filter).first();
    }
    @Override
    public Changes create(Changes currentState, MongoCollection<Changes> mongoCollection) {
      mongoCollection.insertOne(currentState);
      return currentState;
    }
  }
  private static class CopyStateCommand implements StateCommand {
    @Override
    public Changes update(Changes currentState, List<DataTypeCommand> stateChanges, MongoCollection<Changes> mongoCollection, Bson filter) {
      mongoCollection.updateOne(filter, Updates.pushEach(ChangesCodec.CHANGES_VALUES, stateChanges));
      return mongoCollection.find(filter).first();
    }

    @Override
    public Changes create(Changes currentState, MongoCollection<Changes> mongoCollection) {
      return currentState;
    } 
  }
}

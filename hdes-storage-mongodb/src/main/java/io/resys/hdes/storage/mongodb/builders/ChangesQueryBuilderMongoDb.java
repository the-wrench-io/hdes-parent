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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ImmutableChanges;
import io.resys.hdes.storage.api.StorageService;
import io.resys.hdes.storage.api.StorageService.QueryBuilder;
import io.resys.hdes.storage.api.StorageService.TagOperations;
import io.resys.hdes.storage.api.StorageService.TagSupplier;
import io.resys.hdes.storage.api.StorageService.TenantSupplier;
import io.resys.hdes.storage.api.Tag;
import io.resys.hdes.storage.mongodb.codecs.ChangesCodec;
import io.resys.hdes.storage.mongodb.config.MongoDbConfig;

public class ChangesQueryBuilderMongoDb implements StorageService.QueryBuilder {
  private final MongoDbConfig mongoDbConfig;
  private final Single<MongoClient> client;
  private final TenantSupplier tenantSupplier;
  private final TagSupplier tagSupplier;
  private final TagOperations tagOperations;
  private String id;
  private String tag;
  private String label;
  private Integer rev;
  private String tenant;

  public ChangesQueryBuilderMongoDb(
      MongoDbConfig mongoDbConfig, Single<MongoClient> client,
      TenantSupplier tenantSupplier, TagSupplier tagSupplier, TagOperations tagOperations) {
    super();
    this.mongoDbConfig = mongoDbConfig;
    this.client = client;
    this.tenantSupplier = tenantSupplier;
    this.tagSupplier = tagSupplier;
    this.tagOperations = tagOperations;
  }

  @Override
  public QueryBuilder label(String label) {
    this.label = label;
    return this;
  }

  @Override
  public QueryBuilder id(String id) {
    this.id = id;
    return this;
  }

  @Override
  public QueryBuilder tenant(String tenant) {
    this.tenant = tenant;
    return this;
  }

  @Override
  public QueryBuilder tag(String tag) {
    this.tag = tag;
    return this;
  }

  @Override
  public QueryBuilder rev(int rev) {
    this.rev = rev;
    return this;
  }

  @Override
  public Flowable<Changes> get() {
    Assert.isTrue(rev == null || tag == null, () -> "tag and rev can't both be defined");
    
    return client.map(client -> {
      Flowable<Changes> result = Flowable.create(emitter -> {
        final String tenant = this.tenant != null ? this.tenant : tenantSupplier.get();
        final String tagName = this.tag != null ? this.tag : tagSupplier.get();
        final Tag tag = tagName == null ? null : tagOperations.query().name(tagName).get().blockingGet();
        
        final Collection<Bson> filter = new ArrayList<>();
        final Map<String, Integer> tagRevisions;
        if (tag != null) {
          tagRevisions = new HashMap<>();
          filter.add(Filters.or(
              tag.getEntries().stream()
                  .map(entry -> {
                    tagRevisions.put(entry.getId(), entry.getRev());
                    return Filters.eq(ChangesCodec.CHANGES_ID, id);
                  })
                  .collect(Collectors.toList())));
        } else {
          tagRevisions = null;
        }
        if (id != null) {
          filter.add(Filters.eq(ChangesCodec.CHANGES_ID, id));
        }
        if (label != null) {
          filter.add(Filters.eq(ChangesCodec.CHANGES_LABEL, label));
        }
        
        final Consumer<Changes> resultEmitter;
        if(tag != null) {
          resultEmitter = (Changes change) -> emitter
              .onNext(
                  ImmutableChanges.builder()
                  .from(change)
                  .values(new ArrayList<>(change.getValues()).subList(0, tagRevisions.get(change.getId())))
                  .build());
        } else if(rev != null) {
          resultEmitter = (Changes change) -> emitter
              .onNext(
                  ImmutableChanges.builder()
                  .from(change)
                  .values(new ArrayList<>(change.getValues()).subList(0, rev))
                  .build());
        } else {
          resultEmitter = (Changes change) -> emitter.onNext(change);
        }
        
        client.getDatabase(tenant).getCollection(mongoDbConfig.getCollection(), Changes.class)
            .find(Filters.and(filter), Changes.class)
            .forEach(resultEmitter);
        emitter.onComplete();
      }, BackpressureStrategy.BUFFER);
      return result;
    }).blockingGet();
  }
}

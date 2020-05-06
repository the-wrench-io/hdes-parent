package io.resys.hdes.storage.spi.builders;

/*-
 * #%L
 * hdes-storage
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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ChangesRevisionException;
import io.resys.hdes.storage.api.ImmutableChanges;
import io.resys.hdes.storage.api.StorageService.AuthorSupplier;
import io.resys.hdes.storage.api.StorageService.QueryBuilder;
import io.resys.hdes.storage.api.StorageService.SaveBuilder;
import io.resys.hdes.storage.api.StorageService.TenantSupplier;

public class GenericChangesSaveBuilder implements SaveBuilder {
  private final TenantSupplier tenantSupplier;
  private final AuthorSupplier authorSupplier;
  private final Consumer<Changes> consumer;
  private final Supplier<QueryBuilder> queryBuilder;
  private String id;
  private Integer revision;
  private String author;
  private String label;
  private String tenant;
  private Collection<DataTypeCommand> changes;
  
  public GenericChangesSaveBuilder(TenantSupplier tenantSupplier, AuthorSupplier authorSupplier, Consumer<Changes> consumer, Supplier<QueryBuilder> queryBuilder) {
    super();
    this.tenantSupplier = tenantSupplier;
    this.authorSupplier = authorSupplier;
    this.consumer = consumer;
    this.queryBuilder = queryBuilder;
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
    this.changes = changes;
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
    return accept(consumer);
  }
  
  @Override
  public Single<Changes> copy() {
    return accept((c) -> {});
  }
  
  private Single<Changes> accept(Consumer<Changes> consumer) {
    Assert.notNull(changes, () -> "changes can't be null!");
    final String tenant = this.tenant != null ? this.tenant : tenantSupplier.get();
    final String author = this.author != null ? this.author : authorSupplier.get().getId();
    final Collection<DataTypeCommand> commands = this.changes;

    if (id != null) {
      Maybe<Changes> maybe = queryBuilder.get().id(id).get().firstElement().cache();
      if (!maybe.isEmpty().blockingGet()) {
        Assert.notNull(revision, () -> "revision must be defined!");
        Changes oldState = maybe.blockingGet();
        if (oldState.getValues().size() != revision) {
          throw ChangesRevisionException.builder().changes(oldState).revision(revision).build();
        }
        Collection<DataTypeCommand> values = new ArrayList<>(oldState.getValues());
        values.addAll(commands);
        Changes newState = ImmutableChanges.builder().from(oldState).values(values).build();
        consumer.accept(newState);
        return Single.just(newState);
      }
    }
    
    Changes newState = ImmutableChanges.builder().id(id == null ? UUID.randomUUID().toString() : id).label(label).tenant(tenant).values(commands).build();
    consumer.accept(newState);
    return Single.just(newState);
  }
}

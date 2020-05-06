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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ImmutableChanges;
import io.resys.hdes.storage.api.StorageService.QueryBuilder;
import io.resys.hdes.storage.api.StorageService.TagOperations;
import io.resys.hdes.storage.api.StorageService.TagSupplier;
import io.resys.hdes.storage.api.StorageService.TenantSupplier;
import io.resys.hdes.storage.api.Tag;

public class GenericChangesQueryBuilder implements QueryBuilder {
  private final Collection<Changes> changes;
  private final TenantSupplier tenantSupplier;
  private final TagSupplier tagSupplier;
  private final TagOperations tagOperations;
  private String id;
  private String tag;
  private String label;
  private Integer rev;
  private String tenant;

  public GenericChangesQueryBuilder(
      Collection<Changes> changes,
      TenantSupplier tenantSupplier, TagSupplier tagSupplier, TagOperations tagOperations) {
    super();
    this.changes = changes;
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
    Flowable<Changes> result = Flowable.create(emitter -> {
      final String tenant = this.tenant != null ? this.tenant : tenantSupplier.get();
      final String tagName = this.tag != null ? this.tag : tagSupplier.get();
      final Tag tag = tagName == null ? null : tagOperations.query().name(tagName).get().blockingGet();
      
      Predicate<Changes> filter = c -> true;
      final Map<String, Integer> tagRevisions;
      if (tag != null) {
        tagRevisions = tag.getEntries().stream()
        .collect(Collectors.toMap(entry -> entry.getId(), entry -> entry.getRev()));
        
        filter = filter.and((c) -> tagRevisions.containsKey(c.getId()));
      } else {
        tagRevisions = null;
      }
      if (tenant != null) {
        filter = filter.and(c -> c.getTenant().equals(tenant));
      }
      if (id != null) {
        filter = filter.and(c -> c.getId().equals(id));
      }
      if (label != null) {
        filter = filter.and(c -> c.getLabel().equals(label));
      }
      
      final Consumer<Changes> resultEmitter;
      if (tag != null) {
        resultEmitter = (Changes change) -> emitter
            .onNext(
                ImmutableChanges.builder()
                    .from(change)
                    .values(new ArrayList<>(change.getValues()).subList(0, tagRevisions.get(change.getId())))
                    .build());
      } else if (rev != null) {
        resultEmitter = (Changes change) -> emitter
            .onNext(
                ImmutableChanges.builder()
                    .from(change)
                    .values(new ArrayList<>(change.getValues()).subList(0, rev))
                    .build());
      } else {
        resultEmitter = (Changes change) -> emitter.onNext(change);
      }
      changes.stream().filter(filter).forEach(resultEmitter);
      emitter.onComplete();
    }, BackpressureStrategy.BUFFER);
    return result;
  }
}

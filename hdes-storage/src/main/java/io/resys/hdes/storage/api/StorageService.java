package io.resys.hdes.storage.api;

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

import java.util.Collection;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeCommand;

public interface StorageService {
  ChangesOperations changes();

  TagOperations tag();

  interface TagOperations {
    TagBuilder save();

    TagQuery query();
  }

  interface ChangesOperations {
    SaveBuilder save();

    QueryBuilder query();
  }

  interface TagQuery {
    TagQuery name(String name);

    Single<Tag> get();
  }

  interface TagBuilder {
    TagBuilder name(String name);

    TagBuilder desc(String desc);

    /**
     * Creates a tag based on user added entries only. When not used creates tag of
     * all known changes at latest revision.
     * 
     * @param id  identifier of changes
     * @param rev revision id of changes
     */
    TagBuilder addEntry(String id, int rev);

    /**
     * Persists the tag.
     * 
     * @return persisted tag.
     */
    Tag build();
  }

  interface SaveBuilder {
    /**
     * Optional. When not defined creates new empty set of changes to what defined
     * changes are appended. When defined appends the changes to already persisted
     * changes..
     * 
     * @param id identifier of changes to what to append new changes.
     */
    SaveBuilder id(String id);

    /**
     * Optional. When used then must match with already persisted revision(throws
     * exception otherwise).
     * 
     * @param revision identifier after what to append the new changes.
     */
    SaveBuilder revision(int revision);

    /**
     * @param changes list of changes that are appended.
     */
    SaveBuilder changes(Collection<DataTypeCommand> changes);

    /**
     * Required.
     * 
     * @param label group identifier for similar type of resources.
     */
    SaveBuilder label(String label);

    /**
     * Optional.
     * 
     * @param author user id of the changes to be persisted. Default author can be
     *               also configured via implementation.
     */
    SaveBuilder author(String author);

    /**
     * Optional.
     * 
     * @param tenant higher level grouping classifier, when undefined then default
     *               is used.
     */
    SaveBuilder tenant(String tenant);

    /**
     * @return persists defined changes.
     */
    Single<Changes> build();
    
    /**
     * @return does not persist, just combines defined changes.
     */
    Single<Changes> copy();
  }

  interface QueryBuilder {
    QueryBuilder tag(String tag);

    QueryBuilder label(String label);

    QueryBuilder id(String id);

    QueryBuilder rev(int rev);

    QueryBuilder tenant(String tenant);

    Flowable<Changes> get();
  }

  @FunctionalInterface
  interface TenantSupplier {
    String get();
  }

  @FunctionalInterface
  interface TagSupplier {
    String get();
  }

  @FunctionalInterface
  interface AuthorSupplier {
    Author get();
  }

  public static class DefaultTenantSupplier implements TenantSupplier {
    private static final String VALUE = "default";

    @Override
    public String get() {
      return VALUE;
    }
  }
  
  public static class DefaultAuthorSupplier implements AuthorSupplier {
    private static final ImmutableAuthor VALUE = ImmutableAuthor.builder().id("wrench").email("wrench@resys.io").build();
    @Override
    public Author get() {
      return VALUE;
    }
  }
}

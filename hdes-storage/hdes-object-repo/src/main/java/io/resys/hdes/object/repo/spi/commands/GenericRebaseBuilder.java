package io.resys.hdes.object.repo.spi.commands;

/*-
 * #%L
 * hdes-object-repo
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

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.RebaseBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.exceptions.CommitException;
import io.resys.hdes.object.repo.spi.RepoAssert;

public abstract class GenericRebaseBuilder implements RebaseBuilder {
  private final Objects objects;
  private final Supplier<CommitBuilder> commitBuilder;
  private String ref;
  private String author;

  public GenericRebaseBuilder(Objects objects, Supplier<CommitBuilder> commitBuilder) {
    super();
    this.objects = objects;
    this.commitBuilder = commitBuilder;
  }

  @Override
  public RebaseBuilder ref(String ref) {
    this.ref = ref;
    return this;
  }

  @Override
  public RebaseBuilder author(String author) {
    this.author = author;
    return this;
  }

  @Override
  public Objects build() {
    RepoAssert.notNull(author, () -> "author must be defined!");

    RefStatus status = new GenericStatusBuilder(objects).get(ref);
    if(status.getChanges().isEmpty()) {
      throw new CommitException(CommitException.builder().nothingToMerge(ref));
    }
    
    Ref ref = objects.getRefs().get(this.ref);
    CommitBuilder commitBuilder = this.commitBuilder.get()
        .comment("rebase")
        .author(author)
        .parent(ref.getCommit());
    for(Changes changes : status.getChanges()) {
      switch (changes.getAction()) {
      case CREATED:
      case MODIFIED: commitBuilder.add(changes.getName(), changes.getNewValue().get()); break;
      case DELETED: commitBuilder.delete(changes.getName()); break;
      case CONFLICT: commitBuilder.conflict(changes.getName(), changes.getOldValue().get(), changes.getNewValue().get());
      }
    }
    commitBuilder.build();
    return save(Collections.emptyList());
  }
  
  protected abstract Objects save(List<Object> newObjects);
}

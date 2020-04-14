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

import java.util.Optional;
import java.util.function.Supplier;

import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.ChangeAction;
import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.MergeBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.exceptions.CommitException;

public abstract class GenericMergeBuilder implements MergeBuilder {
  private final Objects objects;
  private final Supplier<CommitBuilder> commitBuilder;
  private String ref;
  private String author;

  public GenericMergeBuilder(Objects objects, Supplier<CommitBuilder> commitBuilder) {
    super();
    this.objects = objects;
    this.commitBuilder = commitBuilder;
  }

  @Override
  public MergeBuilder ref(String name) {
    this.ref = name;
    return this;
  }
  
  @Override
  public MergeBuilder author(String author) {
    this.author = author;
    return this;
  }

  @Override
  public Objects build() {
    RefStatus status = new GenericStatusBuilder(objects).get(ref);
    if(status.getChanges().isEmpty()) {
      throw new CommitException(CommitException.builder().nothingToMerge(ref));
    }
    
    Optional<Changes> conflicts = status.getChanges().stream().filter(c -> c.getAction() == ChangeAction.CONFLICT).findFirst();
    if(conflicts.isPresent()) {
      throw new CommitException(CommitException.builder().conflicts(ref));  
    }
    
    StringBuilder comment = new StringBuilder()
        .append("Merged from: ").append(ref)
        .append(", authors: ");
    
    StringBuilder authors = new StringBuilder();
    status.getCommits().stream().forEach(name -> {
      if(authors.length() > 0) {
        authors.append(", ");  
      }
      Commit c = (Commit) objects.getValues().get(name);
      authors.append(c.getAuthor());
    });
    
    CommitBuilder commitBuilder = this.commitBuilder.get()
        .comment(comment.append(authors).toString())
        .merge(status.getCommits().get(0))
        .author(author)
        .parent(objects.getRefs().get(ObjectRepository.MASTER).getCommit());
    for(Changes changes : status.getChanges()) {
      switch (changes.getAction()) {
      case CREATED:
      case MODIFIED: commitBuilder.add(changes.getName(), changes.getNewValue().get()); break;
      case DELETED: commitBuilder.delete(changes.getName()); break;
      default: throw new CommitException(CommitException.builder().conflicts(ref, changes));  
      }
    }
    commitBuilder.build();
    return delete(status);
  }
  
  protected abstract Objects delete(RefStatus ref);
}

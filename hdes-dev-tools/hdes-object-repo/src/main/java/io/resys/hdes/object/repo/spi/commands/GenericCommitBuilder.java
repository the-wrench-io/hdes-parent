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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.resys.hdes.object.repo.api.ImmutableBlob;
import io.resys.hdes.object.repo.api.ImmutableCommit;
import io.resys.hdes.object.repo.api.ImmutableRef;
import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ImmutableTreeEntry;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.CommitException;
import io.resys.hdes.object.repo.api.exceptions.EmptyCommitException;
import io.resys.hdes.object.repo.spi.RepoAssert;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper;

public abstract class GenericCommitBuilder implements CommitBuilder {
  private final static String FAKE_ID = "!-unknown-atm-!";
  
  private final List<TreeEntry> toAdd = new ArrayList<>();
  private final List<Object> newObjects = new ArrayList<>();
  private final List<String> toDelete = new ArrayList<>();
  
  private final Objects objects;
  private final ObjectRepositoryMapper<?> mapper;
  
  private String newRef;
  private String parentId;
  private String author;
  private String comment;
  private String mergeId;

  public GenericCommitBuilder(Objects objects, ObjectRepositoryMapper<?> mapper) {
    super();
    this.objects = objects;
    this.mapper = mapper;
  }

  public Blob blob(String content) {
    return add(mapper.id().id(ImmutableBlob.builder().id(FAKE_ID).value(content).build()));
  }

  @Override
  public CommitBuilder add(String name, String content) {
    Blob blob = blob(content);
    toAdd.add(ImmutableTreeEntry.builder().blob(blob.getId()).name(name).build());
    return this;
  }
  
  @Override
  public CommitBuilder change(String name, String content) {
    Blob blob = blob(content);
    toAdd.add(ImmutableTreeEntry.builder().blob(blob.getId()).name(name).build());
    return this;
  }
  @Override
  public CommitBuilder conflict(String name, String oldValue, String newValue) {
    StringBuilder content = new StringBuilder()
        .append("<<<<<<< master").append("/n")
        .append(oldValue)
        .append("======= ref").append("/n")
        .append(newValue)
        .append(">>>>>>>").append("/n");
    
    Blob blob = blob(content.toString());
    toAdd.add(ImmutableTreeEntry.builder().blob(blob.getId()).name(name).build());
    return this;
  }
  
  @Override
  public CommitBuilder delete(String name) {
    toDelete.add(name);
    return this;
  }

  @Override
  public CommitBuilder parent(String commitId) {
    this.parentId = commitId;
    return this;
  }

  @Override
  public CommitBuilder author(String author) {
    this.author = author;
    return this;
  }

  @Override
  public CommitBuilder ref(String refName) {
    this.newRef = refName;
    return this;
  }
  @Override
  public CommitBuilder comment(String comment) {
    this.comment = comment;
    return this;
  }
  @Override
  public CommitBuilder merge(String mergeId) {
    this.mergeId = mergeId;
    return this;
  }
  @Override
  public Commit build() {
    RepoAssert.notNull(author, () -> "author must be defined!");
    RepoAssert.notNull(comment, () -> "comment must be defined!");
    
    // nothing to commit
    if (toAdd.isEmpty() && toDelete.isEmpty()) {
      throw new EmptyCommitException(parentId, author);
    }
    
    // First commit
    Optional<Commit> parent;
    Optional<Ref> ref;
    Map<String, TreeEntry> oldTree;
    if(objects.getRefs().isEmpty()) {
      oldTree = new HashMap<>();
      ref = Optional.of(ImmutableRef.builder()
          .name(newRef == null || newRef.trim().isEmpty() ? ObjectRepository.MASTER : newRef.trim())
          .commit(FAKE_ID)
          .build());
      parent = Optional.empty();
    } else {
      RepoAssert.notNull(parentId, () -> "parent must be defined!");
      parent = Optional.ofNullable((Commit) objects.getValues().get(parentId));
      if (!parent.isPresent()) {
        throw new CommitException(CommitException.builder().unknownParent(parentId, author));
      }
      if(newRef != null) {
        ref = objects.getRefs().values().stream().filter(h -> h.getName().equals(newRef)).findFirst();
        if(!ref.isPresent()) {
          ref = Optional.of(ImmutableRef.builder().name(newRef).commit(FAKE_ID).build()); 
        }
      } else {
        ref = objects.getRefs().values().stream().filter(h -> h.getCommit().equals(parentId)).findFirst();  
      }
      
      if (!ref.isPresent()) {
        throw new CommitException(CommitException.builder().refDoesNotMatch(parentId, author, objects.getRefs().values()));
      }
      oldTree = ((Tree) objects.getValues().get(parent.get().getTree())).getValues();
    }
    
    // create tree
    
    Map<String, TreeEntry> newTree = new HashMap<>(oldTree);
    
    
    // add
    for (TreeEntry entry : this.toAdd) {
      newTree.put(entry.getName(), entry);
    }
    
    // delete
    for (String entry : this.toDelete) {
      newTree.remove(entry);
    }
    
    Tree tree = add(mapper.id().id(ImmutableTree.builder().id(FAKE_ID).values(newTree).build()));
    
    Commit commit = add(mapper.id().id(ImmutableCommit.builder()
        .id(FAKE_ID)
        .tree(tree.getId())
        .author(author)
        .dateTime(LocalDateTime.now())
        .message(comment)
        .merge(Optional.ofNullable(mergeId))
        .parent(parent.map(e -> e.getId()))
        .build()));
    
    if(objects.getValues().containsKey(commit.getId())) {
      throw new CommitException(CommitException.builder().lastCommitIsIdentical(parentId, author));
    }
    
    add(ImmutableRef.builder()
        .from(ref.get())
        .commit(commit.getId())
        .build());
    
    save(newObjects);
    return commit;
  }
  
  protected abstract Objects save(List<Object> newObjects);
  
  private <T> T add(T object) {
    newObjects.add(object);
    return object;
  }
}

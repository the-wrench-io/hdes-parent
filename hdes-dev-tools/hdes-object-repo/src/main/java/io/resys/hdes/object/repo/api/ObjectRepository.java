package io.resys.hdes.object.repo.api;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

public interface ObjectRepository {
  public static final String MASTER = "master";
  interface IsObject { String getId(); }
  interface IsName { String getName(); }
  enum ChangeAction { MODIFIED, CREATED, DELETED, CONFLICT }
  
  Objects objects();
  Commands commands();
  
  interface Commands {
    StatusBuilder status();
    CommitBuilder commit();
    HistoryBuilder history();
    SnapshotBuilder snapshot();
    CheckoutBuilder checkout();
    PullCommand pull();
    MergeBuilder merge();
    TagBuilder tag();
  }
  
  interface PullCommand {
    Object build();
  }
  
  interface RebaseBuilder {
    RebaseBuilder author(String author);
    RebaseBuilder ref(String refName);
    Objects build();
  }
  
  interface MergeBuilder {
    // ref name from what to merge to "master"
    MergeBuilder ref(String name);
    MergeBuilder author(String author);
    Objects build();
  }
  
  interface StatusBuilder {   
    // Build overview of other refs related to 'master' ref
    List<RefStatus> find();
    
    // Build overview of other ref(name) related to 'master' ref
    RefStatus get(String name);
  }
  
  interface TagBuilder {
    // Name of the tag
    TagBuilder name(String name);
    // optional commit for what to add tag
    TagBuilder commit(String commit);
    // tags can be created only from master
    Tag build();
  }
  
  interface HistoryBuilder {
    List<Commit> build();
  }
  
  interface CheckoutBuilder {
    // tag/commit/ref
    CheckoutBuilder from(String name);
    Objects build();
  }
  
  interface SnapshotBuilder {
    // tag/commit/ref
    SnapshotBuilder from(String name);
    Snapshot build();
  }
  
  interface CommitBuilder {
    CommitBuilder add(String name, String content);
    CommitBuilder delete(String name);
    CommitBuilder change(String name, String content);
    CommitBuilder conflict(String name, String oldValue, String newValue);
    
    CommitBuilder ref(String name);
    CommitBuilder parent(String commitId);
    CommitBuilder author(String author);
    CommitBuilder comment(String message);
    CommitBuilder merge(String commitId);
    Commit build();
  }

  @Value.Immutable
  interface Snapshot {
    String getId();
    Tree getTree();
    List<SnapshotEntry> getValues();
  }
  
  @Value.Immutable
  interface SnapshotEntry {
    String getName();
    String getBlob();
  }

  @Value.Immutable
  interface Head {
    String getValue();
    Snapshot getSnapshot();
  }
  
  @Value.Immutable
  interface Objects {
    Map<String, Ref> getRefs();
    Map<String, Tag> getTags();
    Map<String, IsObject> getValues();
    Optional<Head> getHead();
  }
  
  @Value.Immutable
  interface RefStatus {
    String getName();
    List<String> getCommits();
    List<Changes> getChanges();
  }
  
  @Value.Immutable
  interface Changes {
    String getName();
    ChangeAction getAction();
    Optional<String> getNewValue();
    Optional<String> getOldValue();
  }
  
  
  @Value.Immutable
  interface TreeEntry {
    String getName();
    String getBlob();
  }
  
  @Value.Immutable
  interface Ref extends IsName {
    String getCommit();
  }

  @Value.Immutable
  interface Tag extends IsName {
    String getCommit();
  }
  
  @Value.Immutable
  interface Tree extends IsObject {
    Map<String, TreeEntry> getValues();
  }
  
  @Value.Immutable
  interface Commit extends IsObject {
    String getAuthor();
    LocalDateTime getDateTime();
    String getMessage();
    Optional<String> getParent();
    Optional<String> getMerge();
    String getTree();
  }
  
  @Value.Immutable
  interface Blob extends IsObject {
    String getValue();
  }
}

package io.resys.hdes.assetdb.api;

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

public interface AssetClient {
  public static final String MASTER = "master";
  interface IsObject { String getId(); }
  interface IsName { String getName(); }
  enum ChangeAction { MODIFIED, CREATED, DELETED, CONFLICT }
  
  Objects objects();
  AssetCommands commands();

  // Snapshot of a commit with all of its content
  @Value.Immutable
  interface Snapshot {
    String getId();
    Tree getTree();
    List<SnapshotValue> getValues();
  }
  
  @Value.Immutable
  interface SnapshotValue {
    // name of the resource
    String getName();
    // resource content (not blob id)
    String getContent();
  }

  @Value.Immutable
  interface Head {
    String getName();
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

  // branch with a name
  @Value.Immutable
  interface Ref extends IsName {
    // last commit in the branch
    String getCommit();
  }

  @Value.Immutable
  interface Tag extends IsName {
    // id of a commit
    String getCommit();
  }
  
  // World state 
  @Value.Immutable
  interface Tree extends IsObject {
    // resource name - blob id
    Map<String, TreeValue> getValues();
  }
  
  // Resource name - blob id(content in blob)
  @Value.Immutable
  interface TreeValue {
    // Name of the resource
    String getName();
    // Id of the blob that holds content
    String getBlob();
  }
  
  @Value.Immutable
  interface Commit extends IsObject {
    String getAuthor();
    LocalDateTime getDateTime();
    String getMessage();
    
    // Parent commit id
    Optional<String> getParent();
    
    // This commit is merge commit, that points to a commit in different branch
    Optional<String> getMerge();
    
    // Tree id that describes list of (resource name - content) entries
    String getTree();
  }
  
  // Resource content
  @Value.Immutable
  interface Blob extends IsObject {
    String getValue();
  }
}

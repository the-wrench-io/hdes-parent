package io.resys.hdes.client.git.api;

import java.sql.Timestamp;
import java.util.List;

/*-
 * #%L
 * wrench-component-assets-persistence
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import org.eclipse.jgit.api.Git;
import org.immutables.value.Value;

public interface GitDataSource {
  Git get();
  String getWorkingDir();
  
  String getBlob(String commit, String path);
  GitIntegrationTimestamps getBlobTimestamps(String path);
  
  List<String> getPathRevisions(String path); // list of revisions where the path is present
  List<String> getTags();
  Timestamp getTagCreatedAt(String tagName);
  
  
  void push(GitIntegrationCommit pushMeta);
  void pushTag(String tagName, String annotation, GitIntegrationCommit pushMeta);
  void pushTagDelete(String tagName, GitIntegrationCommit pushMeta);

  @Value.Immutable
  interface GitIntegrationCommit {
    String getAuthorName();
    String getAuthorEmail(); 
    String getMessage(); 
  }
  
  @Value.Immutable
  interface GitIntegrationTimestamps {
    Timestamp getCreated();
    Timestamp getModified();
  }
}

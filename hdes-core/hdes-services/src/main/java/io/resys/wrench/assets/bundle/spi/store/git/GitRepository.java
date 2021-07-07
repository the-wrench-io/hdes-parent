package io.resys.wrench.assets.bundle.spi.store.git;

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

public interface GitRepository {
  Git get();
  List<String> getRev(String path);
  List<String> getTags();
  String getContent(String commit, String path);
  ContentTimestamps getTimestamps(String path, boolean isTag);
  void push();
  void pushTag(String name, String annotation);
  void deleteTag(String name);
  
  String getWorkingDir();
  
  interface ContentTimestamps {
    Timestamp getCreated();
    Timestamp getModified();
  }
}

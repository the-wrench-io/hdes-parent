package io.resys.hdes.projects.api.commands;

/*-
 * #%L
 * hdes-pm-repo
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

import java.util.List;
import java.util.Optional;

import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmRepository.Group;

public interface GroupCommands {
  GroupQueryBuilder query();
  GroupCreateBuilder create();
  GroupUpdateBuilder update();
  GroupDeleteBuilder delete();

  interface GroupQueryBuilder {
    Group id(String id) throws PmException;
    Group rev(String id, String rev);
    Optional<Group> find(String id);
    Optional<Group> findByName(String name);
    
    Group any(String idOrName);
    List<Group> find() throws PmException;
  }  
  
  interface GroupDeleteBuilder {
    GroupDeleteBuilder id(String id);
    GroupDeleteBuilder rev(String rev);
    Group build() throws PmException;
  }  
  
  interface GroupUpdateBuilder {
    GroupUpdateBuilder id(String id);
    GroupUpdateBuilder rev(String rev);
    GroupUpdateBuilder name(String name);
    Group build() throws PmException;
  }  
  
  interface GroupCreateBuilder {
    GroupCreateBuilder name(String name);
    Group build() throws PmException;
  }
}

package io.resys.hdes.pm.repo.api.commands;

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

import io.resys.hdes.pm.repo.api.PmException;
import io.resys.hdes.pm.repo.api.PmRepository.Project;

public interface ProjectCommands {
  ProjectQueryBuilder query();
  ProjectCreateBuilder create();
  ProjectUpdateBuilder update();
  ProjectDeleteBuilder delete();

  interface ProjectQueryBuilder {
    Project id(String id) throws PmException;
    Project rev(String id, String rev) throws PmException;
    Optional<Project> find(String id);
    Optional<Project> findByName(String name);
    List<Project> find();
  }  
  
  interface ProjectDeleteBuilder {
    ProjectDeleteBuilder id(String id);
    ProjectDeleteBuilder rev(String rev);
    Project build() throws PmException;
  }  
  
  interface ProjectUpdateBuilder {
    ProjectUpdateBuilder id(String id);
    ProjectUpdateBuilder rev(String rev);
    ProjectUpdateBuilder name(String name);
    Project build() throws PmException;
  }  
  
  interface ProjectCreateBuilder {
    ProjectCreateBuilder name(String name);
    Project build() throws PmException;
  }
}

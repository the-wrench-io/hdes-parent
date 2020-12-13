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
import io.resys.hdes.pm.repo.api.PmRepository.Access;

public interface AccessCommands {
  AccessQueryBuilder query();
  AccessCreateBuilder create();
  AccessUpdateBuilder update();
  AccessDeleteBuilder delete();

  interface AccessQueryBuilder {
    Access rev(String id, String rev) throws PmException;
    Access id(String id) throws PmException;
    
    Optional<Access> find(String id);
    Optional<Access> findByName(String name);
    
    AccessQueryBuilder projectId(String projectId);
    AccessQueryBuilder userId(String userId);
    List<Access> find() throws PmException;
  }  
  
  interface AccessDeleteBuilder {
    AccessDeleteBuilder rev(String id, String rev);
    AccessDeleteBuilder projectId(String projectId);
    AccessDeleteBuilder userId(String userId);
    List<Access> build() throws PmException;
  }  
  
  interface AccessUpdateBuilder {
    AccessUpdateBuilder id(String id);
    AccessUpdateBuilder rev(String rev);
    AccessUpdateBuilder name(String name);
    Access build() throws PmException;
  }  
  
  interface AccessCreateBuilder {
    AccessCreateBuilder name(String name);
    AccessCreateBuilder projectId(String projectId);
    AccessCreateBuilder userId(String userId);
    Access build() throws PmException;
  }
}

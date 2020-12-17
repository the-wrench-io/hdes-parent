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
import io.resys.hdes.pm.repo.api.PmRepository.GroupUser;

public interface GroupUserCommands {
  GroupUserQueryBuilder query();
  GroupUserCreateBuilder create();
  GroupUserDeleteBuilder delete();

  interface GroupUserQueryBuilder {
    GroupUser rev(String id, String rev) throws PmException;
    GroupUser id(String id) throws PmException;
    
    Optional<GroupUser> find(String id) throws PmException;
    
    GroupUserQueryBuilder groupId(String groupId);
    GroupUserQueryBuilder userId(String userId);
    List<GroupUser> find() throws PmException;
  }  
  
  interface GroupUserDeleteBuilder {
    GroupUserDeleteBuilder rev(String id, String rev);
    GroupUserDeleteBuilder groupId(String groupId);
    GroupUserDeleteBuilder userId(String userId);
    List<GroupUser> build() throws PmException;
  }  
  
  interface GroupUserCreateBuilder {
    GroupUserCreateBuilder groupId(String groupId);
    GroupUserCreateBuilder userId(String userId);
    GroupUser build() throws PmException;
  }
}

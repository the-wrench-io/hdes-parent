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
import io.resys.hdes.pm.repo.api.PmRepository.User;

public interface UserCommands {
  UserQueryBuilder query();
  UserCreateBuilder create();
  UserUpdateBuilder update();
  UserDeleteBuilder delete();

  interface UserQueryBuilder {
    User id(String id) throws PmException;
    User rev(String id, String rev) throws PmException;
    User any(String idOrValueOrExternalIdOrToken) throws PmException;
    
    Optional<User> find(String id);
    Optional<User> findByToken(String token);
    Optional<User> findByValue(String name);
    Optional<User> findByExternalId(String externalId);
    List<User> find() throws PmException;
  }  
  
  interface UserDeleteBuilder {
    UserDeleteBuilder rev(String id, String rev);
    User build() throws PmException;
  }  
  
  interface UserUpdateBuilder {
    UserUpdateBuilder rev(String id, String rev);
    UserUpdateBuilder externalId(String externalId);
    UserUpdateBuilder value(String value);
    UserUpdateBuilder token(String token);
    User build() throws PmException;
  }  
  
  interface UserCreateBuilder {
    UserCreateBuilder externalId(String externalId);
    UserCreateBuilder value(String value);
    User build() throws PmException;
  }
}

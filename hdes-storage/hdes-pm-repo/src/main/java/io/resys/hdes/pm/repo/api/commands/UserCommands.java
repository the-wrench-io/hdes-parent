package io.resys.hdes.pm.repo.api.commands;

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

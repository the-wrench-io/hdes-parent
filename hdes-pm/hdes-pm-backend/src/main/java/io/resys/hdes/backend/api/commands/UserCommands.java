package io.resys.hdes.backend.api.commands;

import java.util.List;
import java.util.Optional;

import io.resys.hdes.backend.api.PmException;
import io.resys.hdes.backend.api.PmRepository.User;

public interface UserCommands {
  UserQueryBuilder query();
  UserCreateBuilder create();
  UserUpdateBuilder update();
  UserDeleteBuilder delete();

  interface UserQueryBuilder {
    User id(String id) throws PmException;
    User rev(String id, String rev) throws PmException;
    Optional<User> find(String id);
    Optional<User> findByValue(String name);
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
    User build() throws PmException;
  }  
  
  interface UserCreateBuilder {
    UserCreateBuilder externalId(String externalId);
    UserCreateBuilder value(String value);
    User build() throws PmException;
  }
}

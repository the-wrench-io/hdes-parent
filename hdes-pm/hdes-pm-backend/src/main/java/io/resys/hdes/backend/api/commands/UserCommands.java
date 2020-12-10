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
    UserQueryBuilder projectId(String projectId);
    User id(String id) throws PmException;
    Optional<User> find(String id) throws PmException;
    List<User> list() throws PmException;
  }  
  
  interface UserDeleteBuilder {
    UserDeleteBuilder id(String id);
    UserDeleteBuilder rev(String rev);
    User build() throws PmException;
  }  
  
  interface UserUpdateBuilder {
    UserUpdateBuilder id(String id);
    UserUpdateBuilder rev(String rev);
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

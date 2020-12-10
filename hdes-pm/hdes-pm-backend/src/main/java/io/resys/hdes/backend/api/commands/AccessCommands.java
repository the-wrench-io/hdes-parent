package io.resys.hdes.backend.api.commands;

import java.util.List;
import java.util.Optional;

import io.resys.hdes.backend.api.PmException;
import io.resys.hdes.backend.api.PmRepository.Access;

public interface AccessCommands {
  AccessQueryBuilder query();
  AccessCreateBuilder create();
  AccessUpdateBuilder update();
  AccessDeleteBuilder delete();

  interface AccessQueryBuilder {
    AccessQueryBuilder projectId(String projectId);
    AccessQueryBuilder userId(String userId);
    
    Access id(String id) throws PmException;
    Optional<Access> find(String id) throws PmException;
    List<Access> list() throws PmException;
  }  
  
  interface AccessDeleteBuilder {
    AccessDeleteBuilder id(String id);
    AccessDeleteBuilder rev(String rev);
    AccessDeleteBuilder projectId(String projectId);
    AccessDeleteBuilder userId(String userId);
    Access build() throws PmException;
  }  
  
  interface AccessUpdateBuilder {
    AccessUpdateBuilder id(String id);
    AccessUpdateBuilder rev(String rev);
    AccessUpdateBuilder name(String name);
    AccessUpdateBuilder token(String token);
    Access build() throws PmException;
  }  
  
  interface AccessCreateBuilder {
    AccessCreateBuilder name(String name);
    AccessCreateBuilder projectId(String projectId);
    AccessCreateBuilder userId(String userId);
    Access build() throws PmException;
  }
}

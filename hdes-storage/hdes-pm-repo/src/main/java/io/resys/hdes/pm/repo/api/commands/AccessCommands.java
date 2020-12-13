package io.resys.hdes.pm.repo.api.commands;

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

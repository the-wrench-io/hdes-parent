package io.resys.hdes.backend.api.commands;

import java.util.List;
import java.util.Optional;

import io.resys.hdes.backend.api.PmException;
import io.resys.hdes.backend.api.PmRepository.Project;

public interface ProjectCommands {
  ProjectQueryBuilder query();
  ProjectCreateBuilder create();
  ProjectUpdateBuilder update();
  ProjectDeleteBuilder delete();

  interface ProjectQueryBuilder {
    Project id(String id) throws PmException;
    Optional<Project> find(String id);
    Optional<Project> findByName(String name);
    List<Project> list();
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

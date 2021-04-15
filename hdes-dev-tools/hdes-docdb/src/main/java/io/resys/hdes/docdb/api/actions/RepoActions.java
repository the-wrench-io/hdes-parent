package io.resys.hdes.docdb.api.actions;

import java.util.List;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.api.models.Repo.RepoHeadState;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface RepoActions {

  QueryBuilder query();
  CreateBuilder create();
  UpdateBuilder update();
  StateBuilder state();
  
  interface StateBuilder {
    StateBuilder repo(String repo);
    StateBuilder head(String head);
    Multi<RepoHeadState> find();
    Uni<RepoHeadState> get();
  }
  
  interface QueryBuilder {
    QueryBuilder id(String id);
    QueryBuilder rev(String rev);
    Multi<Repo> find();
    Uni<Repo> get();
  }
  
  interface CreateBuilder {
    CreateBuilder name(String name);
    Uni<RepoResult> build();
  }
  
  interface UpdateBuilder {
    UpdateBuilder id(String id);
    UpdateBuilder rev(String rev);
    UpdateBuilder name(String name);
    Uni<RepoResult> build();
  }
  
  enum RepoStatus {
    OK, CONFLICT
  }
  
  @Value.Immutable
  interface RepoResult {
    Repo getRepo();
    RepoStatus getStatus();
    List<Message> getMessages();
  }
}
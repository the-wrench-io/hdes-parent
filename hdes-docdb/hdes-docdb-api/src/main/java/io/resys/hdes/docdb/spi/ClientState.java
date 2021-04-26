package io.resys.hdes.docdb.spi;

import io.resys.hdes.docdb.api.models.Repo;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ClientState {
  ClientCollections getCollections();
  RepoBuilder repos();
  
  Uni<ClientInsertBuilder> insert(String repoNameOrId);
  ClientInsertBuilder insert(Repo repo);
  
  Uni<ClientQuery> query(String repoNameOrId);
  ClientQuery query(Repo repo);
  
  ClientRepoState withRepo(Repo repo);
  Uni<ClientRepoState> withRepo(String repoNameOrId);
  
  interface RepoBuilder {
    Uni<Repo> getByName(String name);
    Uni<Repo> getByNameOrId(String nameOrId);
    Multi<Repo> find();
    Uni<Repo> insert(Repo newRepo);
  }
  
  interface ClientRepoState {
    ClientInsertBuilder insert();
    ClientQuery query();
  }
}

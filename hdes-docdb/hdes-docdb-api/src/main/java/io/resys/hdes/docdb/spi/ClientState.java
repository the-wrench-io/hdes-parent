package io.resys.hdes.docdb.spi;

import io.resys.hdes.docdb.api.models.Repo;
import io.smallrye.mutiny.Uni;

public interface ClientState {
  ClientCollections getCollections();
  Uni<Repo> getRepo(String repoNameOrId);
  
  ClientInsertBuilder insert(String repoNameOrId);
  Uni<ClientInsertBuilder> insert(Repo repo);
  
  Uni<ClientQuery> query(String repoNameOrId);
  ClientQuery query(Repo repo);
  
  ClientRepoState withRepo(Repo repo);
  Uni<ClientRepoState> withRepo(String repoNameOrId);
  
  
  interface ClientRepoState {
    ClientInsertBuilder insert();
    ClientQuery query();
  }
}

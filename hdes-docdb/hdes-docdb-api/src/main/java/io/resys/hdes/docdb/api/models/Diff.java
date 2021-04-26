package io.resys.hdes.docdb.api.models;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Objects.Commit;

@Value.Immutable
public interface Diff {
  enum DiffActionType { MODIFIED, CREATED, DELETED, RENAMED }
  enum DivergenceType { BEHIND, AHEAD, EQUAL, CONFLICT }

  Repo getRepo();
  List<Divergence> getDivergences();
  
  @Value.Immutable
  interface Divergence {
    DivergenceType getType();
    DivergenceRef getHead(); // current head commit
    DivergenceRef getMain(); // commit from where divergence starts
    List<DiffAction> getActions(); // only if loaded
  }
  
  @Value.Immutable
  interface DivergenceRef {
    List<String> getRefs();
    List<String> getTags();
    Integer getCommits();
    Commit getCommit();
  }
  
  @Value.Immutable
  interface DiffAction {
    DiffActionType getType();
    @Nullable
    DiffBlob getValue();
    @Nullable
    DiffBlob getTarget();
  }
  
  @Value.Immutable
  interface DiffBlob {
    String getId();
    String getName();
    String getContent();
  }
}

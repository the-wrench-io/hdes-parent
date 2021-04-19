package io.resys.hdes.docdb.api.actions;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects;
import io.resys.hdes.docdb.api.models.Objects.RefObjects;
import io.resys.hdes.docdb.api.models.Repo;
import io.smallrye.mutiny.Uni;

public interface ObjectsActions {
  RepoStateBuilder repoState();
  RefStateBuilder refState();
  
  // build world state
  interface RepoStateBuilder {
    RepoStateBuilder repo(String repoName);
    Uni<ObjectsResult<Objects>> build();
  }

  // build REF world state, no blobs by default
  interface RefStateBuilder {
    RefStateBuilder repo(String repoName);
    RefStateBuilder ref(String ref);
    RefStateBuilder blobs();
    RefStateBuilder blobs(boolean load);
    Uni<ObjectsResult<RefObjects>> build();
  }
  
  enum ObjectsStatus {
    OK, ERROR
  }
  
  @Value.Immutable
  interface ObjectsResult<T> {
    @Nullable
    Repo getRepo();    
    @Nullable
    T getObjects();
    ObjectsStatus getStatus();
    List<Message> getMessages();
  }
}

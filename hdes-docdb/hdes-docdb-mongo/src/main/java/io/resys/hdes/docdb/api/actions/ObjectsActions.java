package io.resys.hdes.docdb.api.actions;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.api.models.Repo;
import io.smallrye.mutiny.Uni;

public interface ObjectsActions {
  RepoStateBuilder repoState();
  RefStateBuilder refState();
  CommitStateBuilder commitState();
  
  // build world state
  interface RepoStateBuilder {
    RepoStateBuilder repo(String repoName);
    Uni<ObjectsResult<Objects>> build();
  }

  // build REF world state, no blobs by default
  interface CommitStateBuilder {
    CommitStateBuilder repo(String repoName);
    CommitStateBuilder anyId(String refOrCommitOrTag);
    CommitStateBuilder blobs();
    CommitStateBuilder blobs(boolean load);
    Uni<ObjectsResult<CommitObjects>> build();
  }
  
  // build REF world state, no blobs by default
  interface RefStateBuilder {
    RefStateBuilder repo(String repoName);
    RefStateBuilder ref(String ref);
    RefStateBuilder blobs();
    RefStateBuilder blobs(boolean load);
    Uni<ObjectsResult<RefObjects>> build();
  }

  @Value.Immutable
  interface CommitObjects {
    Repo getRepo();
    Commit getCommit();
    Tree getTree();
    Map<String, Blob> getBlobs(); //only if loaded
  }

  @Value.Immutable
  interface RefObjects {
    Repo getRepo();
    Ref getRef();
    Commit getCommit();
    Tree getTree();
    Map<String, Blob> getBlobs(); //only if loaded
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

package io.resys.hdes.docdb.api.actions;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Diff;
import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Repo;
import io.smallrye.mutiny.Uni;

public interface DiffActions {

  HeadDiffBuilder head();
  
  interface HeadDiffBuilder {
    HeadDiffBuilder repo(String repoIdOrName);
    HeadDiffBuilder left(String headOrCommitOrTag);
    HeadDiffBuilder right(String headOrCommitOrTag);
    Uni<DiffResult<Diff>> build();
  }
  
  enum DiffStatus {
    OK, ERROR
  }
  
  @Value.Immutable
  interface DiffResult<T> {
    @Nullable
    Repo getRepo();    
    @Nullable
    T getObjects();
    DiffStatus getStatus();
    List<Message> getMessages();
  }
}

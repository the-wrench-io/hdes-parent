package io.resys.hdes.docdb.api.actions;

import io.resys.hdes.docdb.api.models.Diff;
import io.smallrye.mutiny.Uni;

public interface DiffActions {

  RepoDiffBuilder repo();
  
  interface RepoDiffBuilder {
    RepoDiffBuilder repo(String repoIdOrName);
    RepoDiffBuilder left(String headOrCommitOrTag);
    RepoDiffBuilder right(String headOrCommitOrTag);
    Uni<Diff> build();
  }
}

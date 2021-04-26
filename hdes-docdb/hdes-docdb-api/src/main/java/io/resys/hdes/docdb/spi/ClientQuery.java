package io.resys.hdes.docdb.spi;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ClientQuery {
  TagQuery tags();
  CommitQuery commits();
  RefQuery refs();
  TreeQuery trees();
  BlobQuery blobs();
  
  interface RefQuery {
    Uni<Ref> name(String name);
    Uni<Ref> nameOrCommit(String refNameOrCommit);
    Uni<Ref> get();
    Multi<Ref> find();
  }
  
  interface BlobQuery {
    Uni<Blob> id(String blobId);
    Multi<Blob> find();
    Multi<Blob> find(Tree tree);
  }
  
  interface CommitQuery {
    Uni<Commit> id(String commitId);
    Multi<Commit> find();
  }
  
  interface TreeQuery {
    Uni<Tree> id(String treeId);
    Multi<Tree> find();
  }  
  
  interface TagQuery {
    TagQuery name(String name);
    Uni<DeleteResult> delete();
    Uni<Tag> get();
    Multi<Tag> find();
  }
  
  @Value.Immutable
  interface DeleteResult {
    long getDeletedCount();
  }
}

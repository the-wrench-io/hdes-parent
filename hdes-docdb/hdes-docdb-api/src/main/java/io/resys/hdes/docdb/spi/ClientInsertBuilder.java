package io.resys.hdes.docdb.spi;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.smallrye.mutiny.Uni;

public interface ClientInsertBuilder {
  
  Uni<InsertResult> tag(Tag tag);
  Uni<UpsertResult> blob(Blob blob);
  Uni<UpsertResult> ref(Ref ref, Commit commit);
  Uni<UpsertResult> tree(Tree tree);
  Uni<UpsertResult> commit(Commit commit);
  
  enum UpsertStatus { OK, DUPLICATE, ERROR, CONFLICT }
  
  @Value.Immutable
  interface InsertResult {
    boolean getDuplicate();
  } 
  
  @Value.Immutable
  interface UpsertResult {
    String getId();
    boolean isModified();
    Message getMessage();
    Object getTarget();
    UpsertStatus getStatus();
  }
}

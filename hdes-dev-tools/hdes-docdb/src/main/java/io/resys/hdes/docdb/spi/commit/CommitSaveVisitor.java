package io.resys.hdes.docdb.spi.commit;

import io.resys.hdes.docdb.spi.commit.CommitVisitor.CommitOutput;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.smallrye.mutiny.Uni;

public class CommitSaveVisitor {

  private final DocDBClientState state;

  public CommitSaveVisitor(DocDBClientState state) {
    super();
    this.state = state;
  }

  public Uni<CommitOutput> visit(CommitOutput output) {
    
  }
}

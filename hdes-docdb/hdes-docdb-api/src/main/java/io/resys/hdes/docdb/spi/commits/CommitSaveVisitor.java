package io.resys.hdes.docdb.spi.commits;

import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.spi.ClientInsertBuilder.UpsertResult;
import io.resys.hdes.docdb.spi.ClientInsertBuilder.UpsertStatus;
import io.resys.hdes.docdb.spi.ClientState.ClientRepoState;
import io.resys.hdes.docdb.spi.commits.CommitVisitor.CommitOutput;
import io.resys.hdes.docdb.spi.commits.CommitVisitor.CommitOutputStatus;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class CommitSaveVisitor {

  
  private final ClientRepoState state;

  public CommitSaveVisitor(ClientRepoState state) {
    super();
    this.state = state;
  }

  public Uni<CommitOutput> visit(CommitOutput output) {
    // check for consistency
    return state.query().refs().nameOrCommit(output.getRef().getName())
        .onItem().transformToUni(item -> {
          
          // Create new head
          if(item == null && output.getCommit().getParent().isEmpty()) {
            return visitOutput(output);
          }
          // Update head
          if(item != null && output.getCommit().getParent().isPresent() &&
              item.getCommit().equals(output.getCommit().getParent().get())) {
            return visitOutput(output);
          }
          
          StringBuilder error = new StringBuilder();
          if(item == null && output.getCommit().getParent().isPresent()) {
            error.append("Commit points to unknown head: ")
              .append("'")
              .append(output.getRef().getName())
              .append("@")
              .append(output.getCommit().getParent())
              .append("'!");
          } else if(item != null && !item.getCommit().equals(output.getCommit().getParent().get())) {
            error.append("Commit is behind of the head: ")
              .append("'")
              .append(item.getName())
              .append("@")
              .append(item.getCommit())
              .append("'!");            
          }
          
          return Uni.createFrom().item((CommitOutput) ImmutableCommitOutput.builder()
            .from(output)
            .status(CommitOutputStatus.CONFLICT)
            .addMessages(ImmutableMessage.builder().text(error.toString()).build())
            .build());
        });
  }
  
  private Uni<CommitOutput> visitOutput(CommitOutput output) {
    
    // save blobs
    return Multi.createFrom().items(output.getBlobs().stream())
    .onItem().transformToUni(blob -> this.state.insert().blob(blob))
    .merge().collectItems().asList()
    .onItem().transform(upserts -> {
      final var result = ImmutableCommitOutput.builder()
          .from(output)
          .status(CommitOutputStatus.OK);
      upserts.forEach(blob -> result.addMessages(blob.getMessage()));
      return (CommitOutput) result.build();
    })
    
    // save tree
    .onItem().transformToUni(current -> state.insert().tree(current.getTree())
      .onItem().transform(upsert -> (CommitOutput) ImmutableCommitOutput.builder()
        .from(current)
        .status(visitStatus(upsert))
        .addMessages(upsert.getMessage())
        .build())
    )
    
    // save commit
    .onItem().transformToUni(current -> {
      if(current.getStatus() == CommitOutputStatus.OK) {
        return state.insert().commit(current.getCommit())
            .onItem().transform(upsert -> (CommitOutput) ImmutableCommitOutput.builder()
              .from(current)
              .status(visitStatus(upsert))
              .addMessages(upsert.getMessage())
              .build());
      }
      return Uni.createFrom().item(current);
    })
    
    // save ref
    .onItem().transformToUni(current -> {      
      if(current.getStatus() == CommitOutputStatus.OK) {
        return state.insert().ref(output.getRef(), output.getCommit())
            .onItem().transform(upsert -> transformRef(upsert, current));
      }
      return Uni.createFrom().item(current);
    });
  }
  
  private CommitOutput transformRef(UpsertResult upsert, CommitOutput current) {
    return (CommitOutput) ImmutableCommitOutput.builder()
        .from(current)
        .status(visitStatus(upsert))
        .addMessages(upsert.getMessage())
        .build();
  }  
  
  private CommitOutputStatus visitStatus(UpsertResult upsert) {
    if(upsert.getStatus() == UpsertStatus.OK) {
      return CommitOutputStatus.OK;
    } else if(upsert.getStatus() == UpsertStatus.DUPLICATE) {
      return CommitOutputStatus.EMPTY;
    } else if(upsert.getStatus() == UpsertStatus.CONFLICT) {
      return CommitOutputStatus.CONFLICT;
    }
    return CommitOutputStatus.ERROR;
    
  }
}

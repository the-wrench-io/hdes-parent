package io.resys.hdes.docdb.spi.commit;

import org.immutables.value.Value;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.spi.codec.RefCodec;
import io.resys.hdes.docdb.spi.commit.CommitVisitor.CommitOutput;
import io.resys.hdes.docdb.spi.commit.CommitVisitor.CommitOutputStatus;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class CommitSaveVisitor {

  public enum UpsertStatus {
    OK, DUPLICATE, ERROR, CONFLICT
  } 
  
  @Value.Immutable
  public interface UpsertResult {
    String getId();
    boolean isModified();
    Message getMessage();
    Object getTarget();
    UpsertStatus getStatus();
  }
  
  private final DocDBClientState state;

  public CommitSaveVisitor(DocDBClientState state) {
    super();
    this.state = state;
  }

  public Uni<CommitOutput> visit(CommitOutput output) {
    final var ctx = this.state.getContext().toRepo(output.getRepoId());
    // check for consistency
    return state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class)
        .find(Filters.eq(RefCodec.NAME, output.getRef().getName()))
        .collectItems().first().onItem()
        .transformToUni(item -> {
          
          // Create new head
          if(item == null && output.getCommit().getParent().isEmpty()) {
            return visitOutput(ctx, output);
          }
          // Update head
          if(item != null && output.getCommit().getParent().isPresent() &&
              item.getCommit().equals(output.getCommit().getParent().get())) {
            return visitOutput(ctx, output);
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
            .status(CommitOutputStatus.COMFLICT)
            .addMessages(ImmutableMessage.builder().text(error.toString()).build())
            .build());
        });
  }
  
  private Uni<CommitOutput> visitOutput(DocDBContext ctx, CommitOutput output) {
    
    // save blobs
    return Multi.createFrom().items(output.getBlobs().stream())
    .onItem().transformToUni(blob -> saveBlob(ctx, blob))
    .merge().collectItems().asList()
    .onItem().transform(upserts -> {
      final var result = ImmutableCommitOutput.builder()
          .from(output)
          .status(CommitOutputStatus.OK);
      upserts.forEach(blob -> result.addMessages(blob.getMessage()));
      return (CommitOutput) result.build();
    })
    
    // save tree
    .onItem().transformToUni(current -> visitTree(ctx, current.getTree())
      .onItem().transform(upsert -> (CommitOutput) ImmutableCommitOutput.builder()
        .from(current)
        .status(visitStatus(upsert))
        .addMessages(upsert.getMessage())
        .build())
    )
    
    // save commit
    .onItem().transformToUni(current -> {
      if(current.getStatus() == CommitOutputStatus.OK) {
        return visitCommit(ctx, current.getCommit())
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
        return visitRef(ctx, current.getRef(), current.getCommit())
            .onItem().transform(upsert -> (CommitOutput) ImmutableCommitOutput.builder()
              .from(current)
              .status(visitStatus(upsert))
              .addMessages(upsert.getMessage())
              .build());
      }
      return Uni.createFrom().item(current);
    });
  }
  
  private Uni<UpsertResult> visitRef(DocDBContext ctx, Ref ref, Commit commit) {
    return state.getClient()
    .getDatabase(ctx.getDb())
    .getCollection(ctx.getRefs(), Ref.class)
    .find(Filters.eq(RefCodec.NAME, ref.getName()))
    .collectItems().first().onItem()
    .transformToUni(item -> {
      if(item == null) {
        return createRef(ctx, ref);
      }
      return updateRef(ctx, ref);
    });
  }
  
  private Uni<UpsertResult> updateRef(DocDBContext ctx, Ref ref) {
    final var filters = Filters.and(
        Filters.eq(RefCodec.NAME, ref.getName()),
        Filters.eq(RefCodec.COMMIT, ref.getCommit())
      );
    final var updates = Updates.set(RefCodec.COMMIT, ref.getCommit());
    return state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class)
        .updateOne(filters, updates)
        .onItem()
        .transform(updateResult -> {
          if(updateResult.getModifiedCount() == 1) {
            return (UpsertResult) ImmutableUpsertResult.builder()
                .id(ref.getName())
                .isModified(true)
                .status(UpsertStatus.OK)
                .target(ref)
                .message(ImmutableMessage.builder()
                    .text(new StringBuilder()
                        .append("Ref with id:")
                        .append(" '").append(ref.getName()).append("'")
                        .append(" has been updated.")
                        .toString())
                    .build())
                .build();
          }
          return (UpsertResult) ImmutableUpsertResult.builder()
              .id(ref.getName())
              .isModified(false)
              .status(UpsertStatus.CONFLICT)
              .target(ref)
              .message(ImmutableMessage.builder()
                  .text(new StringBuilder()
                      .append("Ref with")
                      .append(" id: '").append(ref.getName()).append("',")
                      .append(" commit: '").append(ref.getCommit()).append("'")
                      .append(" is behind of the head.")
                      .toString())
                  .build())
              .build();
        });
  }
  
  private Uni<UpsertResult> createRef(DocDBContext ctx, Ref ref) {
    return state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class)
        .insertOne(ref)
        .onItem()
        .transform(updateResult -> (UpsertResult) ImmutableUpsertResult.builder()
            .id(ref.getName())
            .isModified(true)
            .target(ref)
            .status(UpsertStatus.OK)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Ref with id:")
                    .append(" '").append(ref.getName()).append("'")
                    .append(" has been created.")
                    .toString())
                .build())
            .build()
        )
        .onFailure(e  -> {
          com.mongodb.MongoWriteException t = (MongoWriteException) e;
          return t.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
        })
        .recoverWithItem(e -> (UpsertResult) ImmutableUpsertResult.builder()
          .id(ref.getName())
          .isModified(false)
          .target(ref)
          .status(UpsertStatus.CONFLICT)
          .message(ImmutableMessage.builder()
              .text(new StringBuilder()
                  .append("Ref with id:")
                  .append(" '").append(ref.getName()).append("'")
                  .append(" is already created.")
                  .toString())
              .build())
          .build());
  } 
    
  
  private Uni<UpsertResult> visitCommit(DocDBContext ctx, Commit commit) {
    return state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class)
        .insertOne(commit)
        .onItem()
        .transform(updateResult -> (UpsertResult) ImmutableUpsertResult.builder()
            .id(commit.getId())
            .isModified(true)
            .target(commit)
            .status(UpsertStatus.OK)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Commit with id:")
                    .append(" '").append(commit.getId()).append("'")
                    .append(" has been saved.")
                    .toString())
                .build())
            .build()
        )
        .onFailure(e  -> {
          com.mongodb.MongoWriteException t = (MongoWriteException) e;
          return t.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
        })
        .recoverWithItem(e -> (UpsertResult) ImmutableUpsertResult.builder()
            .id(commit.getId())
            .isModified(false)
            .target(commit)
            .status(UpsertStatus.CONFLICT)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Commit with id:")
                    .append(" '").append(commit.getId()).append("'")
                    .append(" is already saved.")
                    .toString())
                .build())
            .build());
  }
  
  
  private Uni<UpsertResult> visitTree(DocDBContext ctx, Tree tree) {
    return state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class)
        .insertOne(tree)
        .onItem()
        .transform(updateResult -> (UpsertResult) ImmutableUpsertResult.builder()
            .id(tree.getId())
            .isModified(true)
            .target(tree)
            .status(UpsertStatus.OK)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Tree with id:")
                    .append(" '").append(tree.getId()).append("'")
                    .append(" has been saved.")
                    .toString())
                .build())
            .build()
        )
        .onFailure(e  -> {
          com.mongodb.MongoWriteException t = (MongoWriteException) e;
          return t.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
        })
        .recoverWithItem(e -> (UpsertResult) ImmutableUpsertResult.builder()
            .id(tree.getId())
            .isModified(false)
            .target(tree)
            .status(UpsertStatus.DUPLICATE)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Tree with id:")
                    .append(" '").append(tree.getId()).append("'")
                    .append(" is already saved.")
                    .toString())
                .build())
            .build());
  }
  
  private Uni<UpsertResult> saveBlob(DocDBContext ctx, Blob blob) {
    return state.getClient()
      .getDatabase(ctx.getDb())
      .getCollection(ctx.getBlobs(), Blob.class)
      .insertOne(blob)
      .onItem()
      .transform(updateResult -> (UpsertResult) ImmutableUpsertResult.builder()
          .id(blob.getId())
          .isModified(true)
          .target(blob)
          .status(UpsertStatus.OK)
          .message(ImmutableMessage.builder()
              .text(new StringBuilder()
                  .append("Blob with id:")
                  .append(" '").append(blob.getId()).append("'")
                  .append(" has been saved.")
                  .toString())
              .build())
          .build()
      )
      .onFailure(e  -> {
        if(!(e instanceof com.mongodb.MongoWriteException)) {
          return false;
        }
        com.mongodb.MongoWriteException t = (MongoWriteException) e;
        return t.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
      })
      .recoverWithItem(e -> (UpsertResult) ImmutableUpsertResult.builder()
          .id(blob.getId())
          .isModified(false)
          .target(blob)
          .message(ImmutableMessage.builder()
              .text(new StringBuilder()
                  .append("Blob with id:")
                  .append(" '").append(blob.getId()).append("'")
                  .append(" is already saved.")
                  .toString())
              .build())
          .build());
  }
  
  private CommitOutputStatus visitStatus(UpsertResult upsert) {
    if(upsert.getStatus() == UpsertStatus.OK) {
      return CommitOutputStatus.OK;
    } else if(upsert.getStatus() == UpsertStatus.DUPLICATE) {
      return CommitOutputStatus.EMPTY;
    } else if(upsert.getStatus() == UpsertStatus.CONFLICT) {
      return CommitOutputStatus.COMFLICT;
    }
    return CommitOutputStatus.ERROR;
    
  }
}

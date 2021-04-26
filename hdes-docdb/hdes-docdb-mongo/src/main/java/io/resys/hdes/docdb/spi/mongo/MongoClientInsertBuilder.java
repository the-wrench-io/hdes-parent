package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.spi.ClientInsertBuilder;
import io.resys.hdes.docdb.spi.ImmutableInsertResult;
import io.resys.hdes.docdb.spi.ImmutableUpsertResult;
import io.resys.hdes.docdb.spi.codec.RefCodec;
import io.smallrye.mutiny.Uni;

public class MongoClientInsertBuilder implements ClientInsertBuilder {

  private final MongoClientWrapper wrapper;
  
  public MongoClientInsertBuilder(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public Uni<InsertResult> tag(Tag tag) {
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTags(), Tag.class)
        .insertOne(tag)
        .onItem().transform(inserted -> (InsertResult) ImmutableInsertResult.builder().duplicate(false).build())
        .onFailure(e  -> {
          com.mongodb.MongoWriteException t = (MongoWriteException) e;
          return t.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
        })
        .recoverWithItem(e -> ImmutableInsertResult.builder().duplicate(true).build());
  }

  @Override
  public Uni<UpsertResult> blob(Blob blob) {
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
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
            .status(UpsertStatus.OK)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Blob with id:")
                    .append(" '").append(blob.getId()).append("'")
                    .append(" is already saved.")
                    .toString())
                .build())
            .build());
  }

  public Uni<UpsertResult> ref(Ref ref, Commit commit) {
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
    .getDatabase(ctx.getDb())
    .getCollection(ctx.getRefs(), Ref.class)
    .find(Filters.eq(RefCodec.NAME, ref.getName()))
    .collectItems().first().onItem()
    .transformToUni(item -> {
      if(item == null) {
        return createRef(ref, commit);
      }
      return updateRef(ref, commit);
    });
  }
  
  public Uni<UpsertResult> updateRef(Ref ref, Commit commit) {
    final var filters = Filters.and(
        Filters.eq(RefCodec.NAME, ref.getName()),
        Filters.eq(RefCodec.COMMIT, commit.getParent().get())
      );
    final var updates = Updates.set(RefCodec.COMMIT, ref.getCommit());
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
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
  
  
  private Uni<UpsertResult> createRef(Ref ref, Commit commit) {
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
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

  @Override
  public Uni<UpsertResult> tree(Tree tree) {
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
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
            .status(UpsertStatus.OK)
            .message(ImmutableMessage.builder()
                .text(new StringBuilder()
                    .append("Tree with id:")
                    .append(" '").append(tree.getId()).append("'")
                    .append(" is already saved.")
                    .toString())
                .build())
            .build());
  }

  @Override
  public Uni<UpsertResult> commit(Commit commit) {
    final var ctx = this.wrapper.getNames();
    return wrapper.getClient()
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
  
}

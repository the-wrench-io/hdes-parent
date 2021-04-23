package io.resys.hdes.docdb.spi.objects;

import java.util.Map;
import java.util.stream.Collectors;

import com.mongodb.client.model.Filters;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.resys.hdes.docdb.api.actions.ImmutableCommitObjects;
import io.resys.hdes.docdb.api.actions.ImmutableObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.CommitObjects;
import io.resys.hdes.docdb.api.actions.ObjectsActions.CommitStateBuilder;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.codec.CommitCodec;
import io.resys.hdes.docdb.spi.codec.RefCodec;
import io.resys.hdes.docdb.spi.codec.TreeCodec;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class CommitStateBuilderDefault implements CommitStateBuilder {
  private final DocDBClientState state;
  private String repoName;
  private String refOrCommitOrTag;
  private boolean blobs;
  
  public CommitStateBuilderDefault(DocDBClientState state) {
    super();
    this.state = state;
  }
  @Override
  public CommitStateBuilder repo(String repoName) {
    this.repoName = repoName;
    return this;
  }
  @Override
  public CommitStateBuilder anyId(String refOrCommitOrTag) {
    this.refOrCommitOrTag = refOrCommitOrTag;
    return this;
  }
  @Override
  public CommitStateBuilder blobs(boolean load) {
    this.blobs = load;
    return this;
  }
  @Override
  public CommitStateBuilder blobs() {
    this.blobs = true;
    return this;
  }
  @Override
  public Uni<ObjectsResult<CommitObjects>> build() {
    RepoAssert.notEmpty(repoName, () -> "repoName is not defined!");
    RepoAssert.notEmpty(refOrCommitOrTag, () -> "refOrCommitOrTag is not defined!");
    
    return state.getRepo(repoName).onItem()
    .transformToUni((Repo existing) -> {
      if(existing == null) {
        return Uni.createFrom().item(ImmutableObjectsResult
            .<CommitObjects>builder()
            .status(ObjectsStatus.ERROR)
            .addMessages(RepoException.builder().notRepoWithName(repoName))
            .build());
      }
      final var ctx = state.getContext().toRepo(existing);
      
      return getTagCommit(refOrCommitOrTag, ctx)
        .onItem().transformToUni(tag -> {
          if(tag == null) {
            return getRefCommit(refOrCommitOrTag, ctx);
          }
          return Uni.createFrom().item(tag);
        })
        .onItem().transformToUni(commitId -> {
          if(commitId == null) {
            return getCommit(refOrCommitOrTag, ctx);
          }
          return getCommit(commitId, ctx);
        }).onItem().transformToUni(commit -> {
          if(commit == null) {
            return Uni.createFrom().item(ImmutableObjectsResult
                .<CommitObjects>builder()
                .status(ObjectsStatus.ERROR)
                .addMessages(noCommit(existing))
                .build()); 
          }
          return getState(existing, commit, ctx);
        });
    });
  }
  
  private Message noCommit(Repo repo) {
    return ImmutableMessage.builder()
      .text(new StringBuilder()
      .append("Repo with name: '").append(repo.getName()).append("'")
      .append(" does not contain: tag, ref or commit with id:")
      .append(" '").append(refOrCommitOrTag).append("'")
      .toString())
      .build();
  }
  
  private Uni<ObjectsResult<CommitObjects>> getState(Repo repo, Commit commit, DocDBContext ctx) {
    return getTree(commit, ctx).onItem()
        .transformToUni(tree -> {
          if(this.blobs) {
            return getBlobs(tree, ctx)
              .onItem().transform(blobs -> ImmutableObjectsResult.<CommitObjects>builder()
                .repo(repo)
                .objects(ImmutableCommitObjects.builder()
                    .repo(repo)
                    .tree(tree)
                    .blobs(blobs)
                    .commit(commit)
                    .build())
                .repo(repo)
                .status(ObjectsStatus.OK)
                .build());
          }
          
          return Uni.createFrom().item(ImmutableObjectsResult.<CommitObjects>builder()
            .repo(repo)
            .objects(ImmutableCommitObjects.builder()
                .repo(repo)
                .tree(tree)
                .commit(commit)
                .build())
            .status(ObjectsStatus.OK)
            .build());
        });
  
  }
  private Uni<String> getTagCommit(String tagName, DocDBContext ctx) {
    final ReactiveMongoCollection<Tag> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTags(), Tag.class);
    return collection.find(Filters.eq(RefCodec.NAME, tagName)).collectItems()
        .first().onItem().transform(tag -> tag == null ? null : tag.getCommit());
  }
  private Uni<String> getRefCommit(String refName, DocDBContext ctx) {
    final ReactiveMongoCollection<Ref> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class);
    return collection.find(Filters.eq(RefCodec.NAME, refName)).collectItems()
        .first().onItem().transform(ref -> ref == null ? null : ref.getCommit());
  }
  private Uni<Tree> getTree(Commit commit, DocDBContext ctx) {
    final ReactiveMongoCollection<Tree> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class);
    
    return collection.find(Filters.eq(TreeCodec.ID, commit.getTree())).toUni();
  }
  private Uni<Commit> getCommit(String commit, DocDBContext ctx) {
    final ReactiveMongoCollection<Commit> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class);
    return collection.find(Filters.eq(CommitCodec.ID, commit)).toUni();
  }
  private Uni<Map<String, Blob>> getBlobs(Tree tree, DocDBContext ctx) {
    final ReactiveMongoCollection<Blob> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getBlobs(), Blob.class);
    return collection.find().collectItems().asList().onItem()
        .transform(blobs -> blobs.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)));
  }
}

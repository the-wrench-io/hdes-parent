package io.resys.hdes.docdb.spi.objects;

import java.util.Map;
import java.util.stream.Collectors;

import com.mongodb.client.model.Filters;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.resys.hdes.docdb.api.actions.ImmutableObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.hdes.docdb.api.actions.ObjectsActions.RefStateBuilder;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.ImmutableRefObjects;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.RefObjects;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.codec.CommitCodec;
import io.resys.hdes.docdb.spi.codec.RefCodec;
import io.resys.hdes.docdb.spi.codec.RepoCodec;
import io.resys.hdes.docdb.spi.codec.TreeCodec;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class RefStateBuilderDefault implements RefStateBuilder {
  private final DocDBClientState state;
  private String repoId;
  private String ref;
  private boolean blobs;
  
  public RefStateBuilderDefault(DocDBClientState state) {
    super();
    this.state = state;
  }
  @Override
  public RefStateBuilder repo(String repoId) {
    this.repoId = repoId;
    return this;
  }
  @Override
  public RefStateBuilder ref(String ref) {
    this.ref = ref;
    return this;
  }
  @Override
  public RefStateBuilder blobs(boolean load) {
    this.blobs = load;
    return this;
  }
  @Override
  public RefStateBuilder blobs() {
    this.blobs = true;
    return this;
  }
  @Override
  public Uni<ObjectsResult<RefObjects>> build() {
    RepoAssert.notEmpty(repoId, () -> "repoId is not defined!");
    RepoAssert.notEmpty(ref, () -> "ref is not defined!");
    final var ctx = state.getContext().toRepo(repoId);    
    
    return getRepo(repoId, ctx).collectItems().first().onItem()
    .transformToUni((Repo existing) -> {
      if(existing == null) {
        return Uni.createFrom().item(ImmutableObjectsResult
            .<RefObjects>builder()
            .status(ObjectsStatus.ERROR)
            .addMessages(RepoException.builder().notRepoWithId(repoId))
            .build());
      }
      return getRef(existing, ref, ctx);
    });
  }
  
  private Uni<ObjectsResult<RefObjects>> getRef(Repo repo, String refName, DocDBContext ctx) {
    final ReactiveMongoCollection<Ref> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class);
    return collection.find(Filters.eq(RefCodec.NAME, refName)).collectItems().first().onItem()
        .transformToUni(ref -> {
          if(ref == null) {
            return Uni.createFrom().item(ImmutableObjectsResult
                .<RefObjects>builder()
                .status(ObjectsStatus.OK)
                .addMessages(RepoException.builder().noRepoRef(repoId, refName))
                .build());
          }
          return getState(repo, ref, ctx);
        });
  }
  
  private Uni<ObjectsResult<RefObjects>> getState(Repo repo, Ref ref, DocDBContext ctx) {
    Uni<Tree> tree = getTree(repo, ref, ctx);
    
    if(this.blobs) {
      return tree.onItem().transformToUni(currentTree -> getBlobs(repo, ref, currentTree, ctx)
        .onItem().transform(blobs -> ImmutableObjectsResult.<RefObjects>builder()
          .objects(ImmutableRefObjects.builder()
              .repoId(repo.getId())
              .ref(ref)
              .tree(currentTree)
              .blobs(blobs)
              .build())
          .status(ObjectsStatus.OK)
          .build()
        )
      );
    }
    
    return tree.onItem().transform(currentTree -> ImmutableObjectsResult
      .<RefObjects>builder()
      .objects(ImmutableRefObjects.builder()
          .repoId(repo.getId())
          .ref(ref)
          .tree(currentTree)
          .build())
      .status(ObjectsStatus.OK)
      .build());
  }
  private Uni<Tree> getTree(Repo repo, Ref ref, DocDBContext ctx) {
    Uni<Commit> commit = getCommit(repo, ref, ctx);
    
    final ReactiveMongoCollection<Tree> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class);
    
    return commit.onItem().transformToUni(currentCommit -> collection
        .find(Filters.eq(TreeCodec.ID, currentCommit.getTree())).toUni());
  }
  private Uni<Commit> getCommit(Repo repo, Ref ref, DocDBContext ctx) {
    final ReactiveMongoCollection<Commit> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class);
    return collection.find(Filters.eq(CommitCodec.ID, ref.getCommit())).toUni();
  }
  private Uni<Map<String, Blob>> getBlobs(Repo repo, Ref ref, Tree tree, DocDBContext ctx) {
    final ReactiveMongoCollection<Blob> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getBlobs(), Blob.class);
    return collection.find().collectItems().asList().onItem()
        .transform(blobs -> blobs.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)));
  }
  private Multi<Repo> getRepo(String repoId, DocDBContext ctx) {
    final ReactiveMongoCollection<Repo> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRepos(), Repo.class);
    return collection.find(Filters.eq(RepoCodec.ID, repoId));
  }
}

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
import io.resys.hdes.docdb.spi.codec.TreeCodec;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class RefStateBuilderDefault implements RefStateBuilder {
  private final DocDBClientState state;
  private String repoName;
  private String ref;
  private boolean blobs;
  
  public RefStateBuilderDefault(DocDBClientState state) {
    super();
    this.state = state;
  }
  @Override
  public RefStateBuilder repo(String repoName) {
    this.repoName = repoName;
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
    RepoAssert.notEmpty(repoName, () -> "repoName is not defined!");
    RepoAssert.notEmpty(ref, () -> "ref is not defined!");
    
    return state.getRepo(repoName).onItem()
    .transformToUni((Repo existing) -> {
      if(existing == null) {
        return Uni.createFrom().item(ImmutableObjectsResult
            .<RefObjects>builder()
            .status(ObjectsStatus.ERROR)
            .addMessages(RepoException.builder().notRepoWithName(repoName))
            .build());
      }
      return getRef(existing, ref, state.getContext().toRepo(existing));
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
                .repo(repo)
                .status(ObjectsStatus.OK)
                .addMessages(RepoException.builder().noRepoRef(repo.getName(), refName))
                .build());
          }
          
          return getState(repo, ref, ctx);
        });
  }
  
  private Uni<ObjectsResult<RefObjects>> getState(Repo repo, Ref ref, DocDBContext ctx) {
    return getCommit(ref, ctx).onItem()
        .transformToUni(commit -> getTree(commit, ctx).onItem()
        .transformToUni(tree -> {
          if(this.blobs) {
            return getBlobs(tree, ctx)
              .onItem().transform(blobs -> ImmutableObjectsResult.<RefObjects>builder()
                .repo(repo)
                .objects(ImmutableRefObjects.builder()
                    .repo(repo)
                    .ref(ref)
                    .tree(tree)
                    .blobs(blobs)
                    .commit(commit)
                    .build())
                .repo(repo)
                .status(ObjectsStatus.OK)
                .build());
          }
          
          return Uni.createFrom().item(ImmutableObjectsResult.<RefObjects>builder()
            .repo(repo)
            .objects(ImmutableRefObjects.builder()
                .repo(repo)
                .ref(ref)
                .tree(tree)
                .commit(commit)
                .build())
            .status(ObjectsStatus.OK)
            .build());
        }));
  
  }
  private Uni<Tree> getTree(Commit commit, DocDBContext ctx) {
    final ReactiveMongoCollection<Tree> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class);
    
    return collection.find(Filters.eq(TreeCodec.ID, commit.getTree())).toUni();
  }
  private Uni<Commit> getCommit(Ref ref, DocDBContext ctx) {
    final ReactiveMongoCollection<Commit> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class);
    return collection.find(Filters.eq(CommitCodec.ID, ref.getCommit())).toUni();
  }
  private Uni<Map<String, Blob>> getBlobs(Tree tree, DocDBContext ctx) {
    final ReactiveMongoCollection<Blob> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getBlobs(), Blob.class);
    return collection.find().collectItems().asList().onItem()
        .transform(blobs -> blobs.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)));
  }
}

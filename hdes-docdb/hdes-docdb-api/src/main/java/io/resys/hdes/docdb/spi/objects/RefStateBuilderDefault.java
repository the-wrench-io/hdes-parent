package io.resys.hdes.docdb.spi.objects;

import java.util.Map;
import java.util.stream.Collectors;

import io.resys.hdes.docdb.api.actions.ImmutableObjectsResult;
import io.resys.hdes.docdb.api.actions.ImmutableRefObjects;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.hdes.docdb.api.actions.ObjectsActions.RefObjects;
import io.resys.hdes.docdb.api.actions.ObjectsActions.RefStateBuilder;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.ClientState;
import io.resys.hdes.docdb.spi.ClientState.ClientRepoState;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class RefStateBuilderDefault implements RefStateBuilder {
  private final ClientState state;
  private String repoName;
  private String ref;
  private boolean blobs;
  
  public RefStateBuilderDefault(ClientState state) {
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
    
    return state.repos().getByNameOrId(repoName).onItem()
    .transformToUni((Repo existing) -> {
      if(existing == null) {
        return Uni.createFrom().item(ImmutableObjectsResult
            .<RefObjects>builder()
            .status(ObjectsStatus.ERROR)
            .addMessages(RepoException.builder().notRepoWithName(repoName))
            .build());
      }
      return getRef(existing, ref, state.withRepo(existing));
    });
  }
  
  private Uni<ObjectsResult<RefObjects>> getRef(Repo repo, String refName, ClientRepoState ctx) {

    return ctx.query().refs().name(refName).onItem()
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
  
  private Uni<ObjectsResult<RefObjects>> getState(Repo repo, Ref ref, ClientRepoState ctx) {
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
  private Uni<Tree> getTree(Commit commit, ClientRepoState ctx) {
    return ctx.query().trees().id(commit.getTree());
  }
  private Uni<Commit> getCommit(Ref ref, ClientRepoState ctx) {
    return ctx.query().commits().id(ref.getCommit());
  }
  private Uni<Map<String, Blob>> getBlobs(Tree tree, ClientRepoState ctx) {
    return ctx.query().blobs().find(tree).collectItems().asList().onItem()
        .transform(blobs -> blobs.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)));
  }
}

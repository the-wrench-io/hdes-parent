package io.resys.hdes.docdb.spi.objects;

import java.util.stream.Collectors;

import com.mongodb.client.model.Filters;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.resys.hdes.docdb.api.actions.ImmutableObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.hdes.docdb.api.actions.ObjectsActions.RepoStateBuilder;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.ImmutableObjects;
import io.resys.hdes.docdb.api.models.Objects;
import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.codec.RepoCodec;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class RepoStateBuilderDefault implements RepoStateBuilder {
  private final DocDBClientState state;
  private String repoId;

  public RepoStateBuilderDefault(DocDBClientState state) {
    super();
    this.state = state;
  }
  @Override
  public RepoStateBuilder repo(String repoId) {
    this.repoId = repoId;
    return this;
  }
  @Override
  public Uni<ObjectsResult<Objects>> build() {
    RepoAssert.notEmpty(repoId, () -> "repoId not defined!");
    final var ctx = state.getContext().toRepo(repoId);    
    
    return getRepo(repoId, ctx).collectItems().first().onItem()
    .transformToUni((Repo existing) -> {
      if(existing == null) {
        return Uni.createFrom().item(ImmutableObjectsResult
            .<Objects>builder()
            .status(ObjectsStatus.ERROR)
            .addMessages(RepoException.builder().notRepoWithId(repoId))
            .build());
      }
      return getState(existing, ctx);
    });
  }
  
  private Uni<ObjectsResult<Objects>> getState(Repo repo, DocDBContext ctx) {
    final Uni<Objects> objects = Uni.combine().all().unis(
        getRefs(repo, ctx),
        getTags(repo, ctx),
        getBlobs(repo, ctx),
        getTrees(repo, ctx),
        getCommits(repo, ctx)
    ).combinedWith(raw -> {
      final var builder = ImmutableObjects.builder();
      raw.stream().map(r -> (Objects) r).forEach(r -> builder
          .putAllRefs(r.getRefs())
          .putAllTags(r.getTags())
          .putAllValues(r.getValues())
      );
      return builder.build();
    });
    
    return objects.onItem().transform(state -> ImmutableObjectsResult
      .<Objects>builder()
      .objects(state)
      .status(ObjectsStatus.OK)
      .build());
  }
  
  private Uni<Objects> getRefs(Repo repo, DocDBContext ctx) {
    final ReactiveMongoCollection<Ref> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class);
    return collection.find().collectItems().asList().onItem()
        .transform(refs -> ImmutableObjects.builder()
            .putAllRefs(refs.stream().collect(Collectors.toMap(r -> r.getName(), r -> r)))
            .build());
  }
  private Uni<Objects> getTags(Repo repo, DocDBContext ctx) {
    final ReactiveMongoCollection<Tag> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTags(), Tag.class);
    return collection.find().collectItems().asList().onItem()
        .transform(refs -> ImmutableObjects.builder()
            .putAllTags(refs.stream().collect(Collectors.toMap(r -> r.getName(), r -> r)))
            .build());
  }
  private Uni<Objects> getBlobs(Repo repo, DocDBContext ctx) {
    final ReactiveMongoCollection<Blob> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getBlobs(), Blob.class);
    return collection.find().collectItems().asList().onItem()
        .transform(blobs -> ImmutableObjects.builder()
            .putAllValues(blobs.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)))
            .build());
  }
  private Uni<Objects> getTrees(Repo repo, DocDBContext ctx) {
    final ReactiveMongoCollection<Tree> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class);
    return collection.find().collectItems().asList().onItem()
        .transform(trees -> ImmutableObjects.builder()
            .putAllValues(trees.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)))
            .build());
  }
  private Uni<Objects> getCommits(Repo repo, DocDBContext ctx) {
    final ReactiveMongoCollection<Commit> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class);
    return collection.find().collectItems().asList().onItem()
        .transform(commits -> ImmutableObjects.builder()
            .putAllValues(commits.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)))
            .build());
  }
  
  private Multi<Repo> getRepo(String repoId, DocDBContext ctx) {
    final ReactiveMongoCollection<Repo> collection = state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRepos(), Repo.class);
    return collection.find(Filters.eq(RepoCodec.ID, repoId));
  }
}

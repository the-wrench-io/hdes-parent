package io.resys.hdes.docdb.spi.objects;

import java.util.stream.Collectors;

import io.resys.hdes.docdb.api.actions.ImmutableObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.hdes.docdb.api.actions.ObjectsActions.RepoStateBuilder;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.ImmutableObjects;
import io.resys.hdes.docdb.api.models.Objects;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.ClientState;
import io.resys.hdes.docdb.spi.ClientState.ClientRepoState;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class RepoStateBuilderDefault implements RepoStateBuilder {
  private final ClientState state;
  private String repoName;

  public RepoStateBuilderDefault(ClientState state) {
    super();
    this.state = state;
  }
  @Override
  public RepoStateBuilder repo(String repoName) {
    this.repoName = repoName;
    return this;
  }
  @Override
  public Uni<ObjectsResult<Objects>> build() {
    RepoAssert.notEmpty(repoName, () -> "repoName not defined!");
    
    return state.repos().getByNameOrId(repoName).onItem().transformToUni((Repo existing) -> {
          
      if(existing == null) {
        return Uni.createFrom().item(ImmutableObjectsResult
            .<Objects>builder()
            .status(ObjectsStatus.ERROR)
            .addMessages(RepoException.builder().notRepoWithName(repoName))
            .build());
      }
      return getState(existing, state.withRepo(existing));
    });
  }
  
  private Uni<ObjectsResult<Objects>> getState(Repo repo, ClientRepoState ctx) {
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
  
  private Uni<Objects> getRefs(Repo repo, ClientRepoState ctx) {
    return ctx.query().refs().find().collectItems().asList().onItem()
        .transform(refs -> ImmutableObjects.builder()
            .putAllRefs(refs.stream().collect(Collectors.toMap(r -> r.getName(), r -> r)))
            .build());
  }
  private Uni<Objects> getTags(Repo repo, ClientRepoState ctx) {
    return ctx.query().tags().find().collectItems().asList().onItem()
        .transform(refs -> ImmutableObjects.builder()
            .putAllTags(refs.stream().collect(Collectors.toMap(r -> r.getName(), r -> r)))
            .build());
  }
  private Uni<Objects> getBlobs(Repo repo, ClientRepoState ctx) {
    return ctx.query().blobs().find().collectItems().asList().onItem()
        .transform(blobs -> ImmutableObjects.builder()
            .putAllValues(blobs.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)))
            .build());
  }
  private Uni<Objects> getTrees(Repo repo, ClientRepoState ctx) {
    return ctx.query().trees().find().collectItems().asList().onItem()
        .transform(trees -> ImmutableObjects.builder()
            .putAllValues(trees.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)))
            .build());
  }
  private Uni<Objects> getCommits(Repo repo, ClientRepoState ctx) {
    return ctx.query().commits().find().collectItems().asList().onItem()
        .transform(commits -> ImmutableObjects.builder()
            .putAllValues(commits.stream().collect(Collectors.toMap(r -> r.getId(), r -> r)))
            .build());
  }
}

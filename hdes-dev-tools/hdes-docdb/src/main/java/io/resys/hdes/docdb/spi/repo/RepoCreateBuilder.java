package io.resys.hdes.docdb.spi.repo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.resys.hdes.docdb.api.actions.ImmutableRepoResult;
import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.api.actions.RepoActions.CreateBuilder;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoResult;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoStatus;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.ImmutableRepo;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.codec.RepoCodec;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.support.Identifiers;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class RepoCreateBuilder implements RepoActions.CreateBuilder {

  private final DocDBClientState state;
  private String name;
  
  public RepoCreateBuilder(DocDBClientState state) {
    super();
    this.state = state;
  }
  
  @Override
  public CreateBuilder name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Uni<RepoResult> build() {
    RepoAssert.notEmpty(name, () -> "repo name not defined!");
    RepoAssert.isName(name, () -> "repo name has invalid charecters!");
    
    final var collection = getCollection();
    return collection.find(Filters.eq(RepoCodec.NAME, name))
    .collectItems().first().onItem()
    .transformToUni((Repo existing) -> {
      final Uni<RepoResult> result;
      if(existing != null) {
        result = Uni.createFrom().item(ImmutableRepoResult.builder()
            .status(RepoStatus.CONFLICT)
            .addMessages(RepoException.builder().nameNotUnique(existing.getName(), existing.getId()))
            .build());
      } else {
        result = collection.find()
        .collectItems().asList().onItem()
        .transformToUni((allRepos) -> { 
          
          final var newRepo = ImmutableRepo.builder()
              .id(Identifiers.uuid())
              .rev(Identifiers.uuid())
              .name(name)
              .prefix((allRepos.size() + 10) + "_")
              .build();
          
          return collection
            .insertOne(newRepo).onItem()
            .transform((InsertOneResult insertOne) -> (RepoResult) ImmutableRepoResult.builder()
                .repo(newRepo)
                .status(RepoStatus.OK)
                .build());
        });
      }
      return result;
    });
  }

  
  private ReactiveMongoCollection<Repo> getCollection() {
    return state.getClient()
        .getDatabase(state.getContext().getDb())
        .getCollection(state.getContext().getRepos(), Repo.class);
  }
}

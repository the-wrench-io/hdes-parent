package io.resys.hdes.docdb.spi.repo;

import io.resys.hdes.docdb.api.actions.ImmutableRepoResult;
import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.api.actions.RepoActions.CreateBuilder;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoResult;
import io.resys.hdes.docdb.api.actions.RepoActions.RepoStatus;
import io.resys.hdes.docdb.api.exceptions.RepoException;
import io.resys.hdes.docdb.api.models.ImmutableRepo;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.ClientState;
import io.resys.hdes.docdb.spi.support.Identifiers;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class RepoCreateBuilder implements RepoActions.CreateBuilder {

  private final ClientState state;
  private String name;
  
  public RepoCreateBuilder(ClientState state) {
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
    

    return state.repos().getByName(name)
      .onItem().transformToUni((Repo existing) -> {
      
      final Uni<RepoResult> result;
      if(existing != null) {
        result = Uni.createFrom().item(ImmutableRepoResult.builder()
            .status(RepoStatus.CONFLICT)
            .addMessages(RepoException.builder().nameNotUnique(existing.getName(), existing.getId()))
            .build());
      } else {
        result = state.repos().find()
        .collectItems().asList().onItem()
        .transformToUni((allRepos) -> { 
          
          final var newRepo = ImmutableRepo.builder()
              .id(Identifiers.uuid())
              .rev(Identifiers.uuid())
              .name(name)
              .prefix((allRepos.size() + 10) + "_")
              .build();
          
          return state.repos().insert(newRepo)
            .onItem().transform(next -> (RepoResult) ImmutableRepoResult.builder()
                .repo(next)
                .status(RepoStatus.OK)
                .build());
        });
      }
      return result;
    });
  }
}

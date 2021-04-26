package io.resys.hdes.docdb.spi.state;


import org.immutables.value.Value;

import com.mongodb.client.model.Filters;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.codec.RepoCodec;
import io.smallrye.mutiny.Uni;

@Value.Immutable
public abstract class DocDBClientState {
  public abstract ReactiveMongoClient getClient();
  public abstract DocDBContext getContext();
  
  public Uni<Repo> getRepo(String repoNameOrId) {
    final ReactiveMongoCollection<Repo> collection = getClient()
        .getDatabase(this.getContext().getDb())
        .getCollection(this.getContext().getRepos(), Repo.class);
    return collection
        .find(Filters.or(
            Filters.eq(RepoCodec.NAME, repoNameOrId),
            Filters.eq(RepoCodec.ID, repoNameOrId)
        ))
        .collectItems().first();
  }
}

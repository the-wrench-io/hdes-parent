package io.resys.hdes.docdb.spi;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Repo;

@Value.Immutable
public abstract class ClientCollections {
  public abstract String getDb();
  public abstract String getRepos();
  public abstract String getRefs();
  public abstract String getTags();
  public abstract String getBlobs();
  public abstract String getTrees();
  public abstract String getCommits();
  
  public ClientCollections toRepo(Repo repo) {
    String prefix = repo.getPrefix();
    return ImmutableClientCollections.builder()
        .db(this.getDb())
        .repos(this.getRepos())
        .refs(    prefix + "_" + this.getRefs())
        .tags(    prefix + "_" + this.getTags())
        .blobs(   prefix + "_" + this.getBlobs())
        .trees(   prefix + "_" + this.getTrees())
        .commits( prefix + "_" + this.getCommits())
        .build();
  }
}

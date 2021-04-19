package io.resys.hdes.docdb.spi.state;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DocDBContext {
  public abstract String getDb();
  public abstract String getRepos();
  public abstract String getRefs();
  public abstract String getTags();
  public abstract String getBlobs();
  public abstract String getTrees();
  public abstract String getCommits();
  
  public DocDBContext toRepo(String repoId) {
    return ImmutableDocDBContext.builder()
        .db(this.getDb())
        .repos(this.getRepos())
        .refs(    repoId + "_" + this.getRefs())
        .tags(    repoId + "_" + this.getTags())
        .blobs(   repoId + "_" + this.getBlobs())
        .trees(   repoId + "_" + this.getTrees())
        .commits( repoId + "_" + this.getCommits())
        .build();
  }
}

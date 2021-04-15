package io.resys.hdes.docdb.spi;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DocDBContext {
  public abstract String getDb();
  public abstract String getRepos();
  public abstract String getRefs();
  public abstract String getTags();
  public abstract String getObjects();
  
  public DocDBContext toRepo(String repoId) {
    return ImmutableDocDBContext.builder()
        .db(this.getDb())
        .repos(this.getRepos())
        .refs(repoId + "_" + this.getRefs())
        .tags(repoId + "_" + this.getTags())
        .objects(repoId + "_" + this.getObjects())
        .build();
  }
}

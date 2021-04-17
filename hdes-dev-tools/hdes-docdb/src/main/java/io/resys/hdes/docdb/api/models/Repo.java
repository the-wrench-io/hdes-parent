package io.resys.hdes.docdb.api.models;

import org.immutables.value.Value;

@Value.Immutable
public interface Repo {
  String getId();
  String getRev();
  String getPrefix();
  String getName();

  @Value.Immutable
  public interface RepoHeadState {
    String getGid(); // GID
    String getRepoId();
    String getHeadName();
    String getCommit();
    HeadStateKind getKind();
  }

  enum HeadStateKind { ahead, behind, same }
}

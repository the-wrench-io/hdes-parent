package io.resys.hdes.docdb.api.models;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface Repo {
  String getId();
  String getRev();
  String getPrefix();
  String getName();
  List<RepoHead> getHeads();

  @Value.Immutable
  public interface RepoHead {
    String getGid(); // GID
    String getName();
  }
  
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

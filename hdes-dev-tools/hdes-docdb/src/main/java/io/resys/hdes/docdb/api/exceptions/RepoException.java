package io.resys.hdes.docdb.api.exceptions;

import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.api.models.Message;

public class RepoException extends DocDBException {
  private static final long serialVersionUID = 4311634600357697485L;

  public RepoException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public Message notRepoWithName(String repoId) {
      return ImmutableMessage.builder()
            .text(new StringBuilder()
            .append("Repo with name: '").append(repoId).append("' does not exist!")
            .toString())
          .build();
    }
    public Message noRepoRef(String repo, String ref) {
      return ImmutableMessage.builder()
            .text(new StringBuilder()
            .append("Repo with name: '").append(repo).append("',")
            .append(" has no ref: '").append(ref).append("'")
            .append("!")
            .toString())
          .build();
    }
    public Message nameNotUnique(String name, String id) {
      return ImmutableMessage.builder()
            .text(new StringBuilder()
            .append("Repo with name: '").append(name).append("' already exists,")
            .append(" id: '").append(id).append("'")
            .append("!")
            .toString())
          .build();
    }
    public String updateConflict(String id, String dbRev, String userRev, String name) {
      return new StringBuilder()
          .append("Repo with")
          .append(" id: '").append(id).append("'")
          .append(" name: '").append(name).append("'")
          .append(" can't be updated")
          .append(" because of revision conflict")
          .append(" '").append(dbRev).append("' (db) != (user) '").append(userRev).append("'")
          .append("!")
          .toString();
    }
  }
}

package io.resys.hdes.docdb.api.exceptions;

public class RepoException extends DocDBException {
  private static final long serialVersionUID = 4311634600357697485L;

  public RepoException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public String nameNotUnique(String name) {
      return new StringBuilder()
          .append("Repo with name: '").append(name).append("' already exists")
          .append("!")
          .toString();
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

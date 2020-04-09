package io.resys.hdes.object.repo.api.exceptions;

public class EmptyCommitException extends CommitException {
  private static final long serialVersionUID = -2123781385633987779L;

  public EmptyCommitException(String parent, String author) {
    super(new StringBuilder()
        .append("Commit by: ").append(author)
        .append(" that points to: ").append(parent)
        .append(" is rejected because there are no changes!")
        .toString());
  }
}

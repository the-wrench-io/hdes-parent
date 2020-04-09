package io.resys.hdes.object.repo.api.exceptions;

public class RefException extends RepoException {
  private static final long serialVersionUID = -2123781385633987779L;

  public RefException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public String duplicateTag(String tag) {
      return new StringBuilder()
          .append("Tag with name: ").append(tag)
          .append(" already exists!")
          .toString();
    }

    public String refNameMatch(String tag) {
      return new StringBuilder()
          .append("Tag with name: ").append(tag)
          .append(" matches with one of the REF names!")
          .toString();
    }

    public String refUnknown(String ref) {
      return new StringBuilder()
          .append("Ref with name: ").append(ref)
          .append(" is unknown!")
          .toString();
    }
  }
}

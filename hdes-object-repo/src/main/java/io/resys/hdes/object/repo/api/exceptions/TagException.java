package io.resys.hdes.object.repo.api.exceptions;

public class TagException extends RepoException {
  private static final long serialVersionUID = -2123781385633987779L;

  public TagException(String msg) {
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
    public String headNameMatch(String tag) {
      return new StringBuilder()
          .append("Tag with name: ").append(tag)
          .append(" matches with one of the HEAD names!")
          .toString();
    }
  }
}

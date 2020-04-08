package io.resys.hdes.object.repo.api.exceptions;

public class CheckoutException extends RepoException {
  private static final long serialVersionUID = -2123781385633987779L;

  public CheckoutException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public String notFound(String id) {
      return new StringBuilder()
          .append("No tag, head or commit with id: ").append(id)
          .append("!")
          .toString();
    }
  }
}

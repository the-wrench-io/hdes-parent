package io.resys.hdes.object.repo.api.exceptions;

public class RepoException extends RuntimeException {

  private static final long serialVersionUID = -5933566310053854060L;

  public RepoException() {
    super();
  }

  public RepoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RepoException(String message, Throwable cause) {
    super(message, cause);
  }

  public RepoException(String message) {
    super(message);
  }

  public RepoException(Throwable cause) {
    super(cause);
  }
  
}

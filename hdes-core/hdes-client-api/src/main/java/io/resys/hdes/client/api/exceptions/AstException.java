package io.resys.hdes.client.api.exceptions;

public class AstException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  public AstException(String message, Throwable cause) {
    super(message, cause);
  }

  public AstException(String message) {
    super(message);
  }
}

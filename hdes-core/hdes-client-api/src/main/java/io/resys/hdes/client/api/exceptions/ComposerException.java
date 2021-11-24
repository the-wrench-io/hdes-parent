package io.resys.hdes.client.api.exceptions;

public class ComposerException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  public ComposerException(String message, Throwable cause) {
    super(message, cause);
  }

  public ComposerException(String message) {
    super(message);
  }
}

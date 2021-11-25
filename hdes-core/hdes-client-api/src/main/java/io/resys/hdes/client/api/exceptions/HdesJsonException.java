package io.resys.hdes.client.api.exceptions;

public class HdesJsonException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  public HdesJsonException(String message, Throwable cause) {
    super(message, cause);
  }

  public HdesJsonException(String message) {
    super(message);
  }
}

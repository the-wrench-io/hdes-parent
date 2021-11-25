package io.resys.hdes.client.api.exceptions;

public class HdesEntityNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  public HdesEntityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public HdesEntityNotFoundException(String message) {
    super(message);
  }
}

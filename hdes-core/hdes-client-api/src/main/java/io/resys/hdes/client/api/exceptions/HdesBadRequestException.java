package io.resys.hdes.client.api.exceptions;

public class HdesBadRequestException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  public HdesBadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public HdesBadRequestException(String message) {
    super(message);
  }
}

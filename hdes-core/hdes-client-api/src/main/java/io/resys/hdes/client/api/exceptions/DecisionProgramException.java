package io.resys.hdes.client.api.exceptions;

public class DecisionProgramException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  public DecisionProgramException(String message) {
    super(message);
  }
  public DecisionProgramException(String message, Throwable cause) {
    super(message, cause);
  }

}

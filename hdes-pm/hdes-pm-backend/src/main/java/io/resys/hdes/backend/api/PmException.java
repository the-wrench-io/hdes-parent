package io.resys.hdes.backend.api;

public class PmException extends RuntimeException {
  private static final long serialVersionUID = 208954132433481316L;

  private final ExceptionCode code;
  
  public static enum ExceptionCode {
    USER_NOT_FOUND, DUPLICATE_USER,
    PROJECT_NOT_FOUND, DUPLICATE_PROJECT,
    ACCESS_NOT_FOUND, DUPLICATE_ACCESS
  }
  
  public PmException(ExceptionCode code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public PmException(ExceptionCode code, String message) {
    super(message);
    this.code = code;
  }
  
  public ExceptionCode getCode() {
    return code;
  }
}

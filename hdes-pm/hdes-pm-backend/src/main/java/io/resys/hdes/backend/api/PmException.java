package io.resys.hdes.backend.api;

import org.immutables.value.Value;


public class PmException extends RuntimeException {
  private static final long serialVersionUID = 208954132433481316L;

  private final ConstraintViolation value;
  
  public static enum ConstraintType {
    NOT_FOUND, NOT_UNIQUE
  }
  
  public static enum ErrorType {
    PROJECT, USER, ACCESS
  }
  
  public PmException(ConstraintViolation value, String message, Throwable cause) {
    super(message, cause);
    this.value = value;
  }

  public PmException(ConstraintViolation value, String message) {
    super(message);
    this.value = value;
  }
  
  public ConstraintViolation getValue() {
    return value;
  }
  
  
  @Value.Immutable
  public interface ConstraintViolation {
    String getId();
    String getRev();
    ConstraintType getConstraint();
    ErrorType getType();
  }
}

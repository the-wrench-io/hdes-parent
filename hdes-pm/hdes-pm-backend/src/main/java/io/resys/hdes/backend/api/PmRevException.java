package io.resys.hdes.backend.api;

import org.immutables.value.Value;

public class PmRevException extends RuntimeException {
  private static final long serialVersionUID = 208954132433481316L;
  
  private final RevisionConflict conflict;
  
  public PmRevException(RevisionConflict conflict, String message, Throwable cause) {
    super(message, cause);
    this.conflict = conflict;
  }

  public PmRevException(RevisionConflict conflict, String message) {
    super(message);
    this.conflict = conflict;
  }
  
  public RevisionConflict getConflict() {
    return conflict;
  }
    
  @Value.Immutable
  public interface RevisionConflict {
    String getId();
    String getRev();
    RevisionType getType();
  }
  
  public static enum RevisionType {
    PROJECT, USER, ACCESS
  }
}

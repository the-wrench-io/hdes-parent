package io.resys.hdes.docdb.api.exceptions;

public class DocDBException extends RuntimeException {

  private static final long serialVersionUID = -5933566310053854060L;

  public DocDBException() {
    super();
  }

  public DocDBException(String message, Throwable cause) {
    super(message, cause);
  }

  public DocDBException(String message) {
    super(message);
  }

  public DocDBException(Throwable cause) {
    super(cause);
  }

}

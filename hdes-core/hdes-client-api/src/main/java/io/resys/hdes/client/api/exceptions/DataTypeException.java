package io.resys.hdes.client.api.exceptions;

import io.resys.hdes.client.api.ast.AstDataType;

public class DataTypeException extends RuntimeException {
  private static final long serialVersionUID = 1479713119727436525L;
  private final AstDataType dataType;
  private final Object value;

  public DataTypeException(AstDataType dataType, Object value, Exception e) {
    super(e.getMessage(), e);
    this.dataType = dataType;
    this.value = value;
  }

  public AstDataType getDataType() {
    return dataType;
  }

  public Object getValue() {
    return value;
  }
}

package io.resys.hdes.app.service.spi.model;

import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;

public class ModelBuilderNotDefinedException extends HdesException {
  private static final long serialVersionUID = 9163955084870511877L;

  public ModelBuilderNotDefinedException(String message) {
    super(message);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Changes changes;
    
    public Builder changes(Changes changes) {
      this.changes = changes;
      return this;
    }

    public ModelBuilderNotDefinedException build() {
      Assert.notNull(changes, () -> "changes can't be null");
      String message = String.format("Can't create model for id: %s, label: %s!", 
          changes.getId(), changes.getLabel());
      return new ModelBuilderNotDefinedException(message);
    }
  }
}

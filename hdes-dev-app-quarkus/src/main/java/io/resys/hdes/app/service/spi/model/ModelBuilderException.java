package io.resys.hdes.app.service.spi.model;

import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;

public class ModelBuilderException extends HdesException {
  private static final long serialVersionUID = 9163955084870511877L;

  public ModelBuilderException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Changes changes;
    private Exception e;
    
    public Builder changes(Changes changes) {
      this.changes = changes;
      return this;
    }

    public Builder exception(Exception e) {
      this.e = e;
      return this;
    }
    
    public ModelBuilderException build() {
      Assert.notNull(changes, () -> "changes can't be null");
      Assert.notNull(e, () -> "exception can't be null");
      
      String message = String.format("Can't create model for id: %s, label: %s, because: %s!", 
          changes.getId(), changes.getLabel(), e.getMessage());
      return new ModelBuilderException(message, e);
    }
  }
}

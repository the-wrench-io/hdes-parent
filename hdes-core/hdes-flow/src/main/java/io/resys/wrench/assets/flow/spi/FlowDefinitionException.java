package io.resys.wrench.assets.flow.spi;

import io.resys.hdes.client.api.programs.FlowProgram.Step;

public class FlowDefinitionException extends RuntimeException {

  private static final long serialVersionUID = 2978117894572351791L;

  private final Step node;

  public FlowDefinitionException(String message, Step node, Throwable cause) {
    super(message, cause);
    this.node = node;
  }

  public FlowDefinitionException(String message, Throwable cause) {
    super(message, cause);
    this.node = null;
  }

  public FlowDefinitionException(String message, Step node) {
    super(message);
    this.node = node;
  }
  public FlowDefinitionException(String message) {
    super(message);
    this.node = null;
  }
  public Step getNode() {
    return node;
  }
}

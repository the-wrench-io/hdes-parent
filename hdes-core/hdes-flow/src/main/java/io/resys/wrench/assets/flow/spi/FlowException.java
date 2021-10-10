package io.resys.wrench.assets.flow.spi;

import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.Step;

public class FlowException extends RuntimeException {

  private static final long serialVersionUID = 6659681954052672940L;

  private final Step node;
  private final FlowResult flow;
  
  public FlowException(String message, FlowResult flow, Step node, Throwable cause) {
    super(message, cause);
    this.node = node;
    this.flow = flow;
  }
  public FlowException(String message, FlowResult flow, Step node) {
    super(message);
    this.node = node;
    this.flow = flow;
  }
  public FlowException(FlowResult flow, String message) {
    super(message);
    this.node = null;
    this.flow = flow;
  }

  public FlowException(FlowResult flow, String message, Throwable cause) {
    super(message, cause);
    this.node = null;
    this.flow = flow;
  }

  public Step getNode() {
    return node;
  }
  public FlowResult getFlow() {
    return flow;
  }
}

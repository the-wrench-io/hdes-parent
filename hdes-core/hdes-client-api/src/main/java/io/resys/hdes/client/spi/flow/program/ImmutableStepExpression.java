package io.resys.hdes.client.spi.flow.program;

import io.resys.hdes.client.api.programs.ExpressionProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowTaskExpressionContext;
import io.resys.hdes.client.api.programs.FlowProgram.StepExpression;

public class ImmutableStepExpression implements StepExpression {

  private static final long serialVersionUID = -5237729310088388943L;
  private final ExpressionProgram program;

  public ImmutableStepExpression(ExpressionProgram program) {
    this.program = program;
  }
  @Override
  public boolean eval(FlowTaskExpressionContext context) {
    return (boolean) program.run(context).getValue();
  }
}

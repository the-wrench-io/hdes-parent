package io.resys.hdes.ast.api.types;

public interface ServiceTask<C extends ServiceTask.Context, I extends ServiceTask.Input, O extends ServiceTask.Output> {

  O execute(C context, I input);
  
  interface Input {}
  interface Output {}
  interface Context {}
}

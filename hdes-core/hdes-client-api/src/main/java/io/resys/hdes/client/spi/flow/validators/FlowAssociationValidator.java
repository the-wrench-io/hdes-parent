package io.resys.hdes.client.spi.flow.validators;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowInputNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowTaskNode;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandMessage;
import io.resys.hdes.client.api.ast.AstFlow.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.ImmutableFlowAstCommandMessage;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.ast.TypeDef.ValueType;
import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStep;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.util.HdesAssert;

public class FlowAssociationValidator {

  private final AstFlow ast;
  private final Map<String, TypeDef> allParams = new HashMap<>();
  private final Map<String, List<FlowAstCommandMessage>> messages = new HashMap<>();
  private final List<TaskStepToValidate> toValidate = new ArrayList<>();
  
  public FlowAssociationValidator(AstFlow ast) {
    this.ast = ast;
  }

  public void visitStep(FlowProgramStep step, ProgramWrapper<?, ?> wrapper) {
    final var taskModel = ast.getSrc().getTasks().values().stream()
        .filter(t -> t.getId().getValue().equals(step.getId()))
        .findFirst().get();    
    
    for(TypeDef param : wrapper.getAst().get().getHeaders().getReturnDefs()) {
      if(param.getDirection() == Direction.OUT) {
        String name = AstFlowNodesFactory.getStringValue(taskModel.getId()) + "." + param.getName();
        HdesAssert.isTrue(!allParams.containsKey(name), () -> "Can't have duplicate param: " + name + "!");
        allParams.put(name, param);
      }
    }
    toValidate.add(new TaskStepToValidate(step, wrapper, taskModel));
  }
  
  public Map<String, List<FlowAstCommandMessage>> build() {    
    final var node = ast.getSrc();
    Map<String, AstFlowInputNode> unusedInputs = new HashMap<>(node.getInputs());
    for(final var entry : toValidate) {

      // Validate inputs
      final var taskInputs = getTaskServiceInput(entry);
      final var taskModel = entry.getTaskNode();
      
      for(final var input : entry.getWrapper().getAst().get().getHeaders().getAcceptDefs()) {

        if(taskInputs.containsKey(input.getName())) {
          TaskInput taskInput = taskInputs.get(input.getName());
          if(taskInput.getDataType() == null) {
            error(entry, 
                taskInput.getNode().getStart(),
                taskInput.getNode().getSource().getValue().length(),
                "Task: " + taskModel.getKeyword() + ", input: '" + input.getName() + "', type has unknown mapping:'" + taskInput.getNode().getValue() + "'!");
            continue;
          }
          ValueType ref = taskInput.getDataType().getValueType();
          if(input.getValueType() != ref) {            
            error(entry, 
                taskInput.getNode().getStart(),
                taskInput.getNode().getSource().getValue().length(),
                "Task: " + taskModel.getKeyword() + ", input: '" + input.getName() + "', type has wrong type, expecting:'" + input.getValueType() + "' but was: '" + ref + "'!");
          }
          taskInputs.remove(input.getName());
          unusedInputs.remove(taskInput.getDataType().getName());
        } else {
          error(entry, 
              taskModel.getRef().getInputsNode().getStart(),
              taskModel.getRef().getInputsNode().getSource().getValue().length(),
              "Task: " + taskModel.getKeyword() + ", is missing input: '" + input.getName() + "'!");
        }


      }

      // Unused inputs on task
      for(TaskInput input : taskInputs.values()) {
        String inputName = input.getDataType() == null ? input.getNode().getKeyword() : input.getDataType().getName();
        error(entry,
            input.getNode().getStart(),
            input.getNode().getSource().getValue().length(),
            "Task: " + taskModel.getId().getValue() + ", has unused input: '" + inputName + "'!");
      }
    }

    // Unused inputs on task
    for(AstFlowInputNode input : unusedInputs.values()) {
      warning(
          input.getStart(),
          input.getSource().getValue().length(),
          "Input: " + input.getKeyword() + " is unused!");
    }
    
    return messages;
  }



  private Map<String, TaskInput> getTaskServiceInput(TaskStepToValidate toValidate) {

    AstFlowTaskNode taskModel = toValidate.getTaskNode();
    ProgramWrapper<?, ?> wrapper = toValidate.getWrapper();
    
    Map<String, TypeDef> serviceTypes = wrapper.getAst().get()
        .getHeaders().getAcceptDefs().stream()
        .collect(Collectors.toMap(p -> p.getName(), p -> p));

    Map<String, TaskInput> result = new HashMap<>();
    for(Map.Entry<String, AstFlowNode> entry : taskModel.getRef().getInputs().entrySet()) {

      AstFlowNode node = entry.getValue();
      String mappingName = AstFlowNodesFactory.getStringValue(node);
      if(StringUtils.isEmpty(mappingName)) {
        error(toValidate,
          node.getStart(),
          node.getSource().getValue().length(),
          "Task: " + taskModel.getKeyword() + " mapping: '" + entry.getKey() + "' is missing value!");
      } else if(!serviceTypes.containsKey(entry.getKey())) {
        error(toValidate,
          node.getStart(),
          node.getSource().getValue().length(),
          "Task: " + taskModel.getKeyword() + ", has unknown input: '" + entry.getKey() + "'!");
      } else if(allParams.containsKey(mappingName)) {
        result.put(entry.getKey(), new TaskInput(node, allParams.get(mappingName)));
      } else {
        result.put(entry.getKey(), new TaskInput(node, serviceTypes.get(entry.getKey())));
      }
    }
    return result;
  }
  
  private void error(TaskStepToValidate toValidate, int start, int range, String value) {
    messages.add(ImmutableFlowAstCommandMessage.builder()
        .line(start)
        .range(AstFlowNodesFactory.range().build(0, range))
        .type(FlowCommandMessageType.ERROR)
        .value(value)
        .build());
  }

  private void warning(TaskStepToValidate toValidate, int start, int range, String value) {
    messages.add(ImmutableFlowAstCommandMessage.builder()
        .line(start)
        .range(AstFlowNodesFactory.range().build(0, range))
        .type(FlowCommandMessageType.WARNING)
        .value(value)
        .build());
  }

  private static class TaskStepToValidate {
    private final FlowProgramStep step; 
    private final ProgramWrapper<?, ?> wrapper;
    private final AstFlowTaskNode taskNode;
    private TaskStepToValidate(FlowProgramStep step, ProgramWrapper<?, ?> wrapper, AstFlowTaskNode taskNode) {
      super();
      this.step = step;
      this.wrapper = wrapper;
      this.taskNode = taskNode;
    }
    public FlowProgramStep getStep() {
      return step;
    }
    public ProgramWrapper<?, ?> getWrapper() {
      return wrapper;
    }
    public AstFlowTaskNode getTaskNode() {
      return taskNode;
    }
  }
  
  private static class TaskInput {
    private final AstFlowNode node;
    private final TypeDef dataType;
    public TaskInput(AstFlowNode node, TypeDef dataType) {
      super();
      this.node = node;
      this.dataType = dataType;
    }
    public AstFlowNode getNode() {
      return node;
    }
    public TypeDef getDataType() {
      return dataType;
    }
  }
}

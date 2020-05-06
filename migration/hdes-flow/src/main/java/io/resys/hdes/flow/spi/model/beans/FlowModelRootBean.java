package io.resys.hdes.flow.spi.model.beans;

import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.api.FlowModelException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


public class FlowModelRootBean extends FlowModelBean implements FlowModel.Root {
  public static final long serialVersionUID = 8492235102091866790L;
  public static final String KEY_ID = "id";
  public static final String KEY_THEN = "then";
  public static final String KEY_WHEN = "when";
  public static final String KEY_SWITCH = "switch";
  public static final String KEY_DESC = "description";
  public static final String KEY_INPUTS = "inputs";
  public static final String KEY_TASKS = "tasks";
  public static final String KEY_REQ = "required";
  public static final String KEY_TYPE = "type";
  public static final String KEY_DT = "decisionTable";
  public static final String KEY_USER_TASK = "userTask";
  public static final String KEY_REF = "ref";
  public static final String KEY_COLLECTION = "collection";
  public static final String KEY_SERVICE = "service";
  public static final String VALUE_NEXT = "next";
  public static final String VALUE_END = "end";
  public static final String KEY_DEBUG_VALUE = "debugValue";


  private final Collection<FlowModel.InputType> inputTypes;
  private NodeInputs inputs;
  private NodeTasks tasks;
  private String value;
  private int rev;

  public FlowModelRootBean(Collection<FlowModel.InputType> inputTypes) {
    super(null, -2, null, null, null);
    this.inputTypes = inputTypes;
  }

  @Override
  public int getRev() {
    return rev;
  }

  public FlowModelRootBean setRev(int rev) {
    this.rev = rev;
    return this;
  }

  @Override
  public FlowModel getId() {
    return get(KEY_ID);
  }
  @Override
  public FlowModel getDescription() {
    return get(KEY_DESC);
  }
  @Override
  public Map<String, FlowModel.Input> getInputs() {
    return inputs == null ? Collections.emptyMap() : inputs.getInputs();
  }
  @Override
  public Map<String, FlowModel.Task> getTasks() {
    return tasks == null ? Collections.emptyMap() : tasks.getTasks();
  }
  @Override
  public Collection<FlowModel.InputType> getTypes() {
    return inputTypes;
  }
  @Override
  public String getValue() {
    return value;
  }
  public FlowModelRootBean setValue(String value) {
    this.value = value;
    return this;
  }
  @Override
  public FlowModelRootBean setEnd(int value) {
    super.setEnd(value);
    return this;
  }
  @Override
  public FlowModelBean addChild(FlowModelSourceBean source, int indent, String keyword, String value) {
    if(KEY_INPUTS.equals(keyword)) {
      if(inputs == null) {
        inputs = new NodeInputs(source, indent, keyword, value, this);
        addChild(inputs);
      }
      return inputs;
    } else if(KEY_TASKS.equals(keyword)) {
      if(tasks == null) {
        tasks = new NodeTasks(source, indent, keyword, value, this);
        addChild(tasks);
      }
      return tasks;
    }
    return super.addChild(source, indent, keyword, value);
  }

  private static class NodeInputs extends FlowModelBean {
    private static final long serialVersionUID = 8989618439864849749L;
    private final Map<String, FlowModel.Input> inputs = new HashMap<>();
    public NodeInputs(FlowModelSourceBean source, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
    }
    public Map<String, FlowModel.Input> getInputs() {
      return Collections.unmodifiableMap(inputs);
    }
    @Override
    public FlowModelBean addChild(FlowModelSourceBean source, int indent, String keyword, String value) {
      NodeInputBean result = new NodeInputBean(source, indent, keyword, value, this);
      if(inputs.containsKey(result.getKeyword())) {
        throw FlowModelException.builder().msg(String.format("Duplicate input: %s!", result.getKeyword())).build();
      }
      inputs.put(result.getKeyword(), result);
      return addChild(result);
    }
  }

  private static class NodeTasks extends FlowModelBean {
    private static final long serialVersionUID = 2001644047832806256L;
    private final Map<String, FlowModel.Task> tasks = new HashMap<>();
    private int order = 0;
    public NodeTasks(FlowModelSourceBean source, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
    }

    public Map<String, FlowModel.Task> getTasks() {
      return Collections.unmodifiableMap(tasks);
    }
    @Override
    public FlowModelBean addChild(FlowModelSourceBean source, int indent, String keyword, String value) {
      NodeTaskBean result = new NodeTaskBean(source, order++, indent, keyword, value, this);
      tasks.put(result.getKeyword(), result);
      return addChild(result);
    }
  }

  private static class NodeInputBean extends FlowModelBean implements FlowModel.Input {
    private static final long serialVersionUID = 8910489078429824772L;
    public NodeInputBean(FlowModelSourceBean source, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
    }
    @Override
    public FlowModel getRequired() {
      return get(KEY_REQ);
    }

    @Override
    public FlowModel getType() {
      return get(KEY_TYPE);
    }
    @Override
    public FlowModel getDebugValue() {
      return get(KEY_DEBUG_VALUE);
    }
  }

  private static class NodeSwitchBean extends FlowModelBean implements FlowModel.Switch {
    private static final long serialVersionUID = 8910489078429824772L;
    private final int order;

    public NodeSwitchBean(FlowModelSourceBean source, int order, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
      this.order = order;
    }
    @Override
    public FlowModel getThen() {
      return get(KEY_THEN);
    }
    @Override
    public FlowModel getWhen() {
      return get(KEY_WHEN);
    }
    @Override
    public int getOrder() {
      return order;
    }
  }

  private static class NodeCasesBean extends FlowModelBean {
    private static final long serialVersionUID = 2001644047832806256L;
    private final Map<String, FlowModel.Switch> cases = new HashMap<>();
    private int order = 0;
    public NodeCasesBean(FlowModelSourceBean source, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
    }

    public Map<String, FlowModel.Switch> getValues() {
      return Collections.unmodifiableMap(cases);
    }
    @Override
    public FlowModelBean addChild(FlowModelSourceBean source, int indent, String keyword, String value) {
      NodeSwitchBean result = new NodeSwitchBean(source, order++, indent, keyword, value, this);
      cases.put(result.getKeyword(), result);
      return addChild(result);
    }
  }

  private static class NodeTaskBean extends FlowModelBean implements FlowModel.Task {
    private static final long serialVersionUID = 8910489078429824772L;
    private final int order;
    private NodeRefBean decisionTable;
    private NodeRefBean userTask;
    private NodeRefBean service;
    private NodeCasesBean cases;

    public NodeTaskBean(FlowModelSourceBean source, int order, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
      this.order = order;
    }
    @Override
    public FlowModelBean addChild(FlowModelSourceBean source, int indent, String keyword, String value) {
      if(KEY_SWITCH.equals(keyword)) {
        if(cases == null) {
          cases = new NodeCasesBean(source, indent, keyword, value, this);
          addChild(cases);
        }
        return cases;

      } else if(KEY_USER_TASK.equals(keyword)) {
        if(userTask == null) {
          userTask = new NodeRefBean(source, indent, keyword, value, this);
          addChild(userTask);
        }
        return userTask;
      } else if(KEY_DT.equals(keyword)) {
        if(decisionTable == null) {
          decisionTable = new NodeRefBean(source, indent, keyword, value, this);
          addChild(decisionTable);
        }
        return decisionTable;
      } else if(KEY_SERVICE.equals(keyword)) {
        if(service == null) {
          service = new NodeRefBean(source, indent, keyword, value, this);
          addChild(service);
        }
        return service;
      } else if(KEY_ID.equals(keyword)) {
        if(VALUE_END.equalsIgnoreCase(value) || VALUE_NEXT.equalsIgnoreCase(value) ) {
          throw FlowModelException.builder().msg(String.format("Value: %s is reserved and can't be used!", value)).build();
        }
      }
      return super.addChild(source, indent, keyword, value);
    }
    @Override
    public FlowModel getId() {
      return get(KEY_ID);
    }
    @Override
    public FlowModel getThen() {
      return get(KEY_THEN);
    }
    @Override
    public Map<String, FlowModel.Switch> getSwitch() {
      return cases == null ? Collections.emptyMap() : cases.getValues();
    }
    @Override
    public FlowModel.Ref getDecisionTable() {
      return decisionTable;
    }
    @Override
    public FlowModel.Ref getService() {
      return service;
    }
    @Override
    public FlowModel.Ref getUserTask() {
      return userTask;
    }
    @Override
    public FlowModel.Ref getRef() {
      if(userTask != null) {
        return userTask;
      } else if(service != null) {
        return service;
      }
      return decisionTable;
    }
    @Override
    public int getOrder() {
      return order;
    }
  }

  private static class NodeRefBean extends FlowModelBean implements FlowModel.Ref {

    private static final long serialVersionUID = -3601531710393434419L;

    public NodeRefBean(FlowModelSourceBean source, int indent, String keyword, String value, FlowModelBean parent) {
      super(source, indent, keyword, value, parent);
    }
    @Override
    public FlowModel getRef() {
      return get(KEY_REF);
    }
    @Override
    public FlowModel getCollection() {
      return get(KEY_COLLECTION);
    }
    @Override
    public Map<String, FlowModel> getInputs() {
      FlowModel inputs = getInputsNode();
      if(inputs == null) {
        return Collections.emptyMap();
      }
      return inputs.getChildren();
    }
    @Override
    public FlowModel getInputsNode() {
      return get(KEY_INPUTS);
    }
  }
}

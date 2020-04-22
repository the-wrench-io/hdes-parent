package io.resys.hdes.compiler.spi.java;

import java.util.Collection;
import java.util.Optional;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
import io.resys.hdes.compiler.api.DecisionTable;
import io.resys.hdes.compiler.api.Flow;
import io.resys.hdes.compiler.api.Flow.FlowState;
import io.resys.hdes.compiler.api.Flow.FlowTaskState;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.spi.NamingContext;

public class JavaNamingContext implements NamingContext {
  private final String root;
  private final String fl;
  private final String dt;
  private final JavaFlNamingContext flNaming;
  private final JavaDtNamingContext dtNaming;

  public JavaNamingContext(String root, String fl, String dt) {
    super();
    this.root = root;
    this.fl = root + "." + fl;
    this.dt = root + "." + dt;
    this.flNaming = new JavaFlNamingContext(this);
    this.dtNaming = new JavaDtNamingContext(this);
  }
  
  @Override
  public ClassName immutable(ClassName src) {
    return ClassName.get(src.packageName(), "Immutable" + src.simpleName());
  }
  @Override
  public ClassName immutableBuilder(ClassName src) {
    return ClassName.get(src.packageName(), "Immutable" + src.simpleName() + ".Builder");
  }  
  @Override
  public FlNamingContext fl() {
    return flNaming;
  }

  @Override
  public JavaDtNamingContext dt() {
    return dtNaming;
  }

  public static class JavaDtNamingContext implements DtNamingContext {
    private final JavaNamingContext parent;

    public JavaDtNamingContext(JavaNamingContext parent) {
      super();
      this.parent = parent;
    }

    @Override
    public String pkg() {
      return parent.dt;
    }

    @Override
    public TypeName superinterface(DecisionTableBody node) {
      TypeName returnType = output(node);
      if (node.getHitPolicy() instanceof HitPolicyAll) {
        returnType = ParameterizedTypeName.get(ClassName.get(Collection.class), returnType);
      }
      return ParameterizedTypeName.get(ClassName.get(DecisionTable.class), input(node), returnType);
    }

    @Override
    public ClassName impl(DecisionTableBody node) {
      return ClassName.get(parent.dt, "Gen" + node.getId());
    }

    @Override
    public ClassName input(DecisionTableBody node) {
      return input(node.getId());
    }

    @Override
    public ClassName inputSuperinterface(DecisionTableBody node) {
      return ClassName.get(DecisionTable.DecisionTableInput.class);
    }

    @Override
    public ClassName output(DecisionTableBody node) {
      return output(node.getId());
    }

    @Override
    public ClassName outputSuperinterface(DecisionTableBody node) {
      return ClassName.get(DecisionTable.DecisionTableOutput.class);
    }

    @Override
    public ClassName interfaze(DecisionTableBody node) {
      return interfaze(node.getId());
    }
    
    public ClassName interfaze(String node) {
      return ClassName.get(parent.dt, node);
    }
    public ClassName input(String node) {
      return ClassName.get(parent.dt + "." + node, node + "Input");
    }
    public ClassName output(String node) {
      return ClassName.get(parent.dt + "." + node, node + "Output");
    } 
  }

  public static class JavaFlNamingContext implements FlNamingContext {
    private final JavaNamingContext parent;

    public JavaFlNamingContext(JavaNamingContext parent) {
      super();
      this.parent = parent;
    }

    @Override
    public String pkg() {
      return parent.fl;
    }

    @Override
    public ClassName interfaze(FlowBody node) {
      return ClassName.get(parent.fl, node.getId());
    }

    @Override
    public ClassName impl(FlowBody node) {
      return ClassName.get(parent.fl, "Gen" + node.getId());
    }

    @Override
    public ClassName state(FlowBody node) {
      return ClassName.get(interfaze(node).canonicalName(), node.getId() + "State");
    }

    @Override
    public ClassName input(FlowBody node) {
      return ClassName.get(interfaze(node).canonicalName(), node.getId() + "Input");
    }

    @Override
    public ClassName output(FlowBody node) {
      return ClassName.get(interfaze(node).canonicalName(), node.getId() + "Output");
    }

    @Override
    public ClassName taskState(FlowBody body, FlowTaskNode task) {
      return ClassName.get(interfaze(body).canonicalName(), body.getId() + task.getId());
    }

    @Override
    public TypeName superinterface(FlowBody node) {
      return ParameterizedTypeName.get(ClassName.get(Flow.class), input(node), state(node));
    }

    @Override
    public TypeName stateSuperinterface(FlowBody node) {
      return ParameterizedTypeName.get(ClassName.get(FlowState.class), input(node));
    }

    @Override
    public TypeName taskStateSuperinterface(FlowBody body, FlowTaskNode task) {
      return ParameterizedTypeName.get(
          ClassName.get(FlowTaskState.class),
          refInput(task.getRef().get()), refOutput(task.getRef().get()));
    }

    @Override
    public ClassName input(FlowBody node, ObjectTypeDefNode object) {
      return ClassName.get(interfaze(node).canonicalName(), node.getId() + JavaSpecUtil.capitalize(object.getName()) + "Input");
    }

    @Override
    public ClassName ref(TaskRef node) {
      switch (node.getType()) {
      case DECISION_TABLE: return parent.dt().interfaze(node.getValue());
      //case FLOW_TASK: return ClassName.get(parent, node.getValue());
      //case MANUAL_TASK: return ClassName.get(parent, node.getValue());
      //case SERVICE_TASK: return ClassName.get(parent, node.getValue());
      default: throw new HdesCompilerException(HdesCompilerException.builder().unknownFlTaskRef(node));
      }
    }

    @Override
    public String refMethod(TaskRef ref) {
      return JavaSpecUtil.decapitalize(ref(ref).simpleName());
    }

    @Override
    public ClassName refInput(TaskRef node) {
      switch (node.getType()) {
      case DECISION_TABLE: return parent.dt().input(node.getValue());
      //case FLOW_TASK: return ClassName.get(parent, node.getValue());
      //case MANUAL_TASK: return ClassName.get(parent, node.getValue());
      //case SERVICE_TASK: return ClassName.get(parent, node.getValue());
      default: throw new HdesCompilerException(HdesCompilerException.builder().unknownFlTaskRef(node));
      }
    }

    @Override
    public ClassName refOutput(TaskRef node) {
      switch (node.getType()) {
      case DECISION_TABLE: return parent.dt().output(node.getValue());
      //case FLOW_TASK: return ClassName.get(parent, node.getValue());
      //case MANUAL_TASK: return ClassName.get(parent, node.getValue());
      //case SERVICE_TASK: return ClassName.get(parent, node.getValue());
      default: throw new HdesCompilerException(HdesCompilerException.builder().unknownFlTaskRef(node));
      }
    }
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {
    private String root;
    private String flows;
    private String decisionTables;

    public Config root(String root) {
      this.root = root;
      return this;
    }

    public Config flows(String flows) {
      this.flows = flows;
      return this;
    }

    public Config decisionTables(String decisionTables) {
      this.decisionTables = decisionTables;
      return this;
    }

    public JavaNamingContext build() {
      return new JavaNamingContext(
          Optional.ofNullable(root).orElse("io.resys.hdes.compiler"),
          Optional.ofNullable(flows).orElse("fl"),
          Optional.ofNullable(decisionTables).orElse("dt"));
    }
  }
}

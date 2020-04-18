package io.resys.hdes.compiler.spi.java;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.compiler.api.Flow;
import io.resys.hdes.compiler.api.Flow.FlowState;
import io.resys.hdes.compiler.api.Flow.FlowTaskState;
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
  public FlNamingContext fl() {
    return flNaming;
  }
  
  @Override
  public DtNamingContext dt() {
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
      if(node.getHitPolicy() instanceof HitPolicyAll) {
        returnType = ParameterizedTypeName.get(ClassName.get(Collection.class), returnType);
      }
      return ParameterizedTypeName.get(ClassName.get(Function.class), input(node), returnType);
    }
    @Override
    public ClassName impl(DecisionTableBody node) {
      return ClassName.get(parent.dt, "Gen" + node.getId());
    }
    @Override
    public ClassName input(DecisionTableBody node) {
      return ClassName.get(parent.dt, node.getId() + "Input");
    }
    @Override
    public ClassName output(DecisionTableBody node) {
      return ClassName.get(parent.dt, node.getId() + "Output");
    }
    @Override
    public ClassName interfaze(DecisionTableBody node) {
      return ClassName.get(parent.dt, node.getId());
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
      return ClassName.get(parent.fl, node.getId() + "State");
    }
    @Override
    public ClassName input(FlowBody node) {
      return ClassName.get(parent.fl, node.getId() + "Input");
    }
    @Override
    public ClassName output(FlowBody node) {
      return ClassName.get(parent.fl, node.getId() + "Output");
    }
    @Override
    public ClassName taskInput(FlowBody body, FlowTaskNode task) {
      return ClassName.get(parent.fl, body.getId() + task.getId() + "Input");
    }
    @Override
    public ClassName taskOutput(FlowBody body, FlowTaskNode task) {
      return ClassName.get(parent.fl, body.getId() + task.getId() + "Output");
    }
    @Override
    public ClassName taskState(FlowBody body, FlowTaskNode task) {
      return ClassName.get(parent.fl, body.getId() + task.getId());
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
          taskInput(body, task), taskOutput(body, task));
    }
    @Override
    public ClassName input(FlowBody node, ObjectTypeDefNode object) {
      return ClassName.get(parent.fl, node.getId() + JavaSpecUtil.capitalize(object.getName()) + "Input");
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
          Optional.ofNullable(decisionTables).orElse("dt")
          );
    }
  }
}

package io.resys.hdes.compiler.spi;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;

public interface NamingContext {
  
  FlNamingContext fl();
  DtNamingContext dt();
  
  interface DtNamingContext {
    String pkg();
    ClassName interfaze(DecisionTableBody node);
    TypeName superinterface(DecisionTableBody node);
    ClassName impl(DecisionTableBody node);
    ClassName input(DecisionTableBody node);
    ClassName output(DecisionTableBody node);
  }
 
  interface FlNamingContext {
    String pkg();
    
    ClassName interfaze(FlowBody node);
    TypeName superinterface(FlowBody node); 
    
    ClassName state(FlowBody node);
    TypeName stateSuperinterface(FlowBody node);
    
    ClassName impl(FlowBody node);
    ClassName input(FlowBody node);
    ClassName input(FlowBody node, ObjectTypeDefNode object);
    
    ClassName output(FlowBody node);
    
    ClassName taskState(FlowBody body, FlowTaskNode task);
    TypeName taskStateSuperinterface(FlowBody body, FlowTaskNode task);
    
    ClassName taskInput(FlowBody body, FlowTaskNode task);
    ClassName taskOutput(FlowBody body, FlowTaskNode task);
  }
}

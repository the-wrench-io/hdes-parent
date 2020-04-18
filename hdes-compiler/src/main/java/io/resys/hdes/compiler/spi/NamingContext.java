package io.resys.hdes.compiler.spi;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;

public interface NamingContext {
  
  FlNamingContext fl();
 
  interface FlNamingContext {
    ClassName interfaze(FlowBody node);
    TypeName superinterface(FlowBody node); 
    
    ClassName state(FlowBody node);
    TypeName stateSuperinterface(FlowBody node);
    
    ClassName impl(FlowBody node);
    ClassName input(FlowBody node);
    ClassName output(FlowBody node);
    
    ClassName taskState(FlowBody body, FlowTaskNode task);
    TypeName taskStateSuperinterface(FlowBody body, FlowTaskNode task);
    
    ClassName taskInput(FlowBody body, FlowTaskNode task);
    ClassName taskOutput(FlowBody body, FlowTaskNode task);
  }
}

package io.resys.hdes.runtime.spi;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.api.HdesCompiler.TypeDeclaration;
import io.resys.hdes.compiler.api.HdesCompiler.TypeName;
import io.resys.hdes.runtime.api.HdesRuntime;
import io.resys.hdes.runtime.spi.tools.HdesJavaFileManager;
import io.resys.hdes.runtime.spi.tools.HdesJavaFileObject;
import io.resys.hdes.runtime.spi.tools.ImmutableRuntimeEnvir;

public class ImmutableHdesRuntime implements HdesRuntime {
  
  public static Builder builder() {
    return new ImmutableEnvirBuilder();
  }
  
  public static class ImmutableEnvirBuilder implements Builder {

    private final List<Resource> resources = new ArrayList<>();

    @Override
    public RuntimeEnvir build() {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      HdesJavaFileManager fileManager = HdesJavaFileManager.create(compiler);
      
      DiagnosticCollector<Object> diagnosticListener = new DiagnosticCollector<Object>();
      List<String> options = new ArrayList<>();
      StringWriter out = new StringWriter();
      List<String> annotatedClasses = new ArrayList<>();
      List<SimpleJavaFileObject> files = new ArrayList<>();
      Map<String, TypeName> executables = new HashMap<>();
      
      Map<String, Resource> values = new HashMap<>();
      
      for(Resource resource : resources) {
        values.put(resource.getName(), resource);
        
        // Type names for annotation processor
        for(TypeName typeName : resource.getTypes()) {
          annotatedClasses.add(typeName.getPkg() + "." + typeName.getName());
        }
        
        // Java source code
        for(TypeDeclaration typeDeclaration : resource.getDeclarations()) {
          System.out.println(typeDeclaration.getValue());
          files.add(HdesJavaFileObject.create(typeDeclaration.getType().getName(), typeDeclaration.getValue()));
          if(typeDeclaration.isExecutable()) {
            executables.put(resource.getName(), typeDeclaration.getType());
          }
        }
      }
      
      CompilationTask task = compiler.getTask(out, fileManager, diagnosticListener, options, annotatedClasses, files);
      var immutables = new org.immutables.processor.ProxyProcessor(); 
      task.setProcessors(Arrays.asList(immutables));
      task.call();
      
      List<Diagnostic<?>> diagnostics = diagnosticListener.getDiagnostics();  
      return ImmutableRuntimeEnvir.from(fileManager, diagnostics, executables, Collections.unmodifiableMap(values));
    }

    @Override
    public Builder from(List<Resource> resources) {
      if(resources != null) {
        this.resources.addAll(resources);
      }
      return this;
    }
    
  } 
}

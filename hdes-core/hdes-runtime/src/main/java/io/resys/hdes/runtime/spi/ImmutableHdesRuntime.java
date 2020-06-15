package io.resys.hdes.runtime.spi;

import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class ImmutableHdesRuntime implements HdesRuntime {
  
  public static EnvirBuilder builder() {
    return new ImmutableEnvirBuilder();
  }
  
  public static class ImmutableEnvirBuilder implements EnvirBuilder {

    private final List<Resource> resources = new ArrayList<>();

    @Override
    public HdesRuntimeEnvir build() {
      
      Lookup lookup = MethodHandles.lookup();
      ClassLoader classLoader = lookup.lookupClass().getClassLoader();
      
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      HdesJavaFileManager fileManager = HdesJavaFileManager.create(compiler);
      
      // compilation
      DiagnosticCollector diagnosticListener = new DiagnosticCollector();
      List<String> options = new ArrayList<>();
      StringWriter out = new StringWriter();
      List<String> annotatedClasses = new ArrayList<>();
      List<SimpleJavaFileObject> files = new ArrayList<>();
      
      for(Resource resource : resources) {
        for(TypeName typeName : resource.getTypes()) {
          annotatedClasses.add(typeName.getPkg() + "." + typeName.getName());
        }
        
        for(TypeDeclaration typeDeclaration : resource.getDeclarations()) {
          files.add(HdesJavaFileObject.create(typeDeclaration.getType().getName(), typeDeclaration.getValue()));
        }
      }
      
      CompilationTask task = compiler.getTask(out, fileManager, diagnosticListener, options, annotatedClasses, files);
      
      var immutables = new org.immutables.processor.ProxyProcessor(); 
      task.setProcessors(Arrays.asList(immutables));
      task.call();
      
      
      System.out.println(diagnosticListener.getDiagnostics());
      
      return null;
    }

    @Override
    public EnvirBuilder from(List<Resource> resources) {
      if(resources != null) {
        this.resources.addAll(resources);
      }
      return this;
    }
    
  } 
}

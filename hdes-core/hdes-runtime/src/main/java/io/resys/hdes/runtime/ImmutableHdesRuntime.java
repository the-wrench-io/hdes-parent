package io.resys.hdes.runtime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;


public class ImmutableHdesRuntime implements HdesRuntime {
  
  private static final Path REPO = createRepo();
  
  private static Path createRepo() {
    try {
      return Files.createTempDirectory("hdes");
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  public static EnvirBuilder builder() {
    return new ImmutableEnvirBuilder();
  }
  
  
  public static class ImmutableEnvirBuilder implements EnvirBuilder {

    @Override
    public ResourceBuilder add() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public HdesRuntimeEnvir build() {
      Lookup lookup = MethodHandles.lookup();
      ClassLoader classLoader = lookup.lookupClass().getClassLoader();
      
      
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      JavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
      
      
      // compilation
      DiagnosticListener<? super JavaFileObject> diagnosticListener = null;
      List<String> options = new ArrayList<>();
      StringWriter out = new StringWriter();
      Iterable<String> classes = null;
      List<SimpleJavaFileObject> files = new ArrayList<>();
      CompilationTask task = compiler.getTask(out, fileManager, diagnosticListener, options, classes, files);

      
      return null;
    }
    
  } 
}

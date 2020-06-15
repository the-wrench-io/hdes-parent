package io.resys.hdes.runtime.tests;

import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import io.resys.hdes.runtime.HdesJavaFileObject;

public class HdesRuntimeTest {

  @Test
  public void createRepository() {
    Lookup lookup = MethodHandles.lookup();
    ClassLoader classLoader = lookup.lookupClass().getClassLoader();
    
    
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    JavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    
    
    // compilation
    DiagnosticCollector diagnosticListener = new DiagnosticCollector();
    List<String> options = new ArrayList<>();
    StringWriter out = new StringWriter();
    Iterable<String> classes = null;
    List<SimpleJavaFileObject> files = new ArrayList<>();
    
    String test = "package io.resys.hdes.runtime.tests;\n" + 
        "\n" + 
        "import io.resys.hdes.runtime.HdesRuntimeResource;\n" + 
        "\n" + 
        "public class TestClass implements HdesRuntimeResource {\n" + 
        "\n" + 
        "  @Override\n" + 
        "  public String run() {\n" + 
        "    // TODO Auto-generated method stub\n" + 
        "    return \"DATA\";\n" + 
        "  }\n" + 
        "  \n" + 
        "  \n" + 
        "}";
    
    files.add(HdesJavaFileObject.create("TestClass", test));
    
    CompilationTask task = compiler.getTask(out, fileManager, diagnosticListener, options, classes, files);
    task.call();
    
    
    System.out.println(diagnosticListener.getDiagnostics());
  }

}

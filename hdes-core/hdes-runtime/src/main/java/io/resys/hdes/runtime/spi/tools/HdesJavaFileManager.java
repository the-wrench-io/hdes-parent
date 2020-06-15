package io.resys.hdes.runtime.spi.tools;

import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

public class HdesJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final Map<String, JavaFileObject> cache = new HashMap<>();

  public HdesJavaFileManager(StandardJavaFileManager standardManager) {
    super(standardManager);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(
      JavaFileManager.Location location,
      String className,
      JavaFileObject.Kind kind,
      FileObject sibling) {
    HdesJavaFileObject result = HdesJavaFileObject.create(className, kind);
    cache.put(className, result);
    return result;
  }
  
  
  public static HdesJavaFileManager create(JavaCompiler compiler) {
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    return new HdesJavaFileManager(fileManager);
  }
}

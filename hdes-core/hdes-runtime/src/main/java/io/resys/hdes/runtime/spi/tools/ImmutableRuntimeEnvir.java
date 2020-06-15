package io.resys.hdes.runtime.spi.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.compiler.api.HdesCompiler.TypeName;
import io.resys.hdes.compiler.api.HdesWhen;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeEnvir;

public class ImmutableRuntimeEnvir implements RuntimeEnvir {
  private static final Logger LOGGER = LoggerFactory.getLogger(ImmutableRuntimeEnvir.class);
  private final HdesClassLoader classLoader;
  private final Map<String, TypeName> executables;
  
  public ImmutableRuntimeEnvir(HdesClassLoader classLoader, Map<String, TypeName> executables) {
    super();
    this.classLoader = classLoader;
    this.executables = executables;
  }

  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  public <T> T get(String name) throws ClassNotFoundException {
    if(executables.containsKey(name)) {
      TypeName typeName = executables.get(name);
      name = typeName.getPkg() + "." + typeName.getName();
    }
    Class<T> clazz = (Class<T>) classLoader.findClass(name);
    try {
      HdesWhen when = null;
      return (T) clazz.getConstructors()[0].newInstance(when);
    } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static ImmutableRuntimeEnvir from(HdesJavaFileManager fileManager, List<Diagnostic<?>> diagnostics, Map<String, TypeName> executables) {
    if (!diagnostics.isEmpty()) {
      LOGGER.error(diagnostics.toString());
    }
    
    Map<String, HdesJavaFileObject> cache = fileManager.getCache();
    HdesClassLoader classLoader = new HdesClassLoader(cache);
    return new ImmutableRuntimeEnvir(classLoader, executables);
  }

  private static class HdesClassLoader extends ClassLoader {
    private final Map<String, HdesJavaFileObject> cache;

    public HdesClassLoader(Map<String, HdesJavaFileObject> cache) {
      super(HdesClassLoader.class.getClassLoader());
      this.cache = cache;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      HdesJavaFileObject javaFileObject = cache.get(name);
      if (javaFileObject == null) {
        return super.findClass(name);
      }
      byte[] bytes = javaFileObject.getBytes();
      return defineClass(name, bytes, 0, bytes.length);
    }
  }
}

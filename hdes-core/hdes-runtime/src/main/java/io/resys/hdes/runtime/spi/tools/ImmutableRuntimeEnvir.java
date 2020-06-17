package io.resys.hdes.runtime.spi.tools;

/*-
 * #%L
 * hdes-runtime
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.api.HdesCompiler.TypeName;
import io.resys.hdes.compiler.api.HdesExecutable;
import io.resys.hdes.compiler.api.HdesExecutable.Input;
import io.resys.hdes.compiler.api.HdesExecutable.Output;
import io.resys.hdes.compiler.api.HdesWhen;
import io.resys.hdes.compiler.spi.HdesWhenGen;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeEnvir;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeTask;
import io.resys.hdes.runtime.api.ImmutableRuntimeTask;

public class ImmutableRuntimeEnvir implements RuntimeEnvir {
  private static final Logger LOGGER = LoggerFactory.getLogger(ImmutableRuntimeEnvir.class);
  private final HdesClassLoader classLoader;
  private final Map<String, TypeName> executables;
  private final Map<String, Resource> values;
  private final List<Diagnostic<?>> diagnostics;
  
  @Override
  public List<Diagnostic<?>> getDiagnostics() {
    return diagnostics;
  }

  public ImmutableRuntimeEnvir(HdesClassLoader classLoader, Map<String, TypeName> executables, Map<String, Resource> values, List<Diagnostic<?>> diagnostics) {
    super();
    this.classLoader = classLoader;
    this.executables = executables;
    this.values = values;
    this.diagnostics = diagnostics;
  }

  @Override
  public RuntimeTask get(String name) throws ClassNotFoundException {
    String lookup = name;
    if(executables.containsKey(name)) {
      TypeName typeName = executables.get(name);
      lookup = typeName.getPkg() + "." + typeName.getName();
    }
    Class<?> clazz = classLoader.findClass(lookup);
    try {
      Resource resource = values.get(name);
      
      Class<? extends HdesExecutable.Input> input = (Class<? extends Input>) classLoader.findClass(resource.getInput().getPkg() + "$" + resource.getInput().getName());
      Class<? extends HdesExecutable.Output> output = (Class<? extends Output>) classLoader.findClass(resource.getInput().getPkg() + "$" + resource.getOutput().getName());
      
      HdesWhen when = HdesWhenGen.get();
      HdesExecutable executable = (HdesExecutable) clazz.getConstructors()[0].newInstance(when);
      
      return ImmutableRuntimeTask.builder()
          .name(name)
          .input(input)
          .output(output)
          .value(executable)
          .build();
    } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static ImmutableRuntimeEnvir from(
      HdesJavaFileManager fileManager, 
      List<Diagnostic<?>> diagnostics, 
      Map<String, TypeName> executables, 
      Map<String, Resource> values) {
    
    if (!diagnostics.isEmpty()) {
      LOGGER.error(diagnostics.toString());
      System.err.println(diagnostics);
    }
    
    Map<String, HdesJavaFileObject> cache = fileManager.getCache();
    HdesClassLoader classLoader = new HdesClassLoader(cache);
    return new ImmutableRuntimeEnvir(classLoader, executables, values, diagnostics);
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

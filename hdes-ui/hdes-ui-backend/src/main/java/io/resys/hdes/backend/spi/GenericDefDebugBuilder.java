package io.resys.hdes.backend.spi;

/*-
 * #%L
 * hdes-ui-backend
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefDebug;
import io.resys.hdes.backend.api.HdesBackend.DefDebugBuilder;
import io.resys.hdes.backend.api.HdesBackend.DefError;
import io.resys.hdes.backend.api.HdesBackend.DefErrorToken;
import io.resys.hdes.backend.api.HdesBackend.Reader;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.api.ImmutableDefDebug;
import io.resys.hdes.backend.api.ImmutableDefError;
import io.resys.hdes.backend.api.ImmutableDefErrorToken;
import io.resys.hdes.compiler.api.DecisionTable;
import io.resys.hdes.compiler.api.DecisionTable.DecisionTableInput;
import io.resys.hdes.compiler.api.DecisionTable.DecisionTableOutput;
import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeEnvir;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeTask;
import io.resys.hdes.runtime.spi.ImmutableHdesRuntime;
import io.resys.hdes.runtime.spi.tools.HdesJavaFileObject;

public class GenericDefDebugBuilder implements DefDebugBuilder {
  private final static DefErrorToken EMPTY_TOKEN = ImmutableDefErrorToken.builder().line(0).column(0).msg("").text("").build();
  
  private final HdesCompiler compiler;
  private final HdesBackendStorage storage;
  private final Reader reader; 
  
  private String tagOrBranch;
  private String name;
  private byte[] input;

  public GenericDefDebugBuilder(HdesCompiler compiler, HdesBackendStorage storage, Reader reader) {
    super();
    this.compiler = compiler;
    this.storage = storage;
    this.reader = reader;
  }
  @Override
  public DefDebugBuilder qualifier(String tagOrBranch) {
    this.tagOrBranch = tagOrBranch;
    return this;
  }
  @Override
  public DefDebugBuilder name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public DefDebugBuilder input(byte[] input) {
    this.input = input == null ?  new byte[0] : input;
    return this;
  }
  @Override
  public DefDebug build() {
    Collection<Def> defs = storage.read().build();
    Optional<Def> namedDef = defs.stream().filter(def -> def.getName().equals(name)).findFirst();
    
    if(namedDef.isEmpty()) {
      return ImmutableDefDebug.builder().name(name).qualifier(tagOrBranch)
          .errors(Arrays.asList(ImmutableDefError.builder()
          .id("defNotFound").name(name)
          .message("Definition '" + name + "' not found!")
          .token(EMPTY_TOKEN).build())).build();
    }
    
    Collection<Def> linkedDefs = new ArrayList<>(); 
    linkedDefs.add(namedDef.get());
    // TODO:: find dependencies and add to builder
    
    
    // Collect all errors
    List<DefError> defErrors = new ArrayList<>();
    linkedDefs.forEach(d -> defErrors.addAll(d.getErrors()));
    if(!defErrors.isEmpty()) {
      return ImmutableDefDebug.builder().name(name).qualifier(tagOrBranch).errors(defErrors).build();
    }
    
    // Parse to java
    HdesCompiler.Parser parser = compiler.parser();
    linkedDefs.forEach(d -> parser.add(d.getId(), d.getValue()));
    List<Resource> resources = parser.build();
    
    // Compile java resources
    RuntimeEnvir runtimeEnvir = ImmutableHdesRuntime.builder().from(resources).build();
    
    // Filter errors
    List<DefError> errors = runtimeEnvir.getDiagnostics().stream()
    .filter(d -> d.getKind() == Kind.ERROR)
    .map((Diagnostic<?> d) -> {
      HdesJavaFileObject src = (HdesJavaFileObject) d.getSource();
      String className = src.getClassName();
      String msg = d.getMessage(Locale.ENGLISH);
      return ImmutableDefError.builder().id("defCompilationError").name(className).message(msg).token(EMPTY_TOKEN).build();
    })
    .collect(Collectors.toList());
    
    if(!errors.isEmpty()) {
      return ImmutableDefDebug.builder().name(name).qualifier(tagOrBranch).errors(errors).build();
    }
    
    // Run the def
    try {
      RuntimeTask task = runtimeEnvir.get(name);
      
      // TODO, separate task runners for different resource types
      
      // DT only 
      DecisionTableInput dtInput = (DecisionTableInput) reader.build(input, task.getInput());
      DecisionTable dt = (DecisionTable) task.getValue();
      DecisionTableOutput dtOutput = (DecisionTableOutput) dt.apply(input);
      
      return ImmutableDefDebug.builder().name(name).qualifier(tagOrBranch).output(dtOutput).build();
    } catch(ClassNotFoundException e) {
      return ImmutableDefDebug.builder().name(name).qualifier(tagOrBranch)
          .errors(Arrays.asList(ImmutableDefError.builder()
          .id("defDependencyNotFound").name(name)
          .message(e.getMessage())
          .token(EMPTY_TOKEN).build())).build();
    } catch(Exception e) {
      return ImmutableDefDebug.builder().name(name).qualifier(tagOrBranch).errors(
          Arrays.asList(ImmutableDefError.builder()
              .id("defNotFound").name(name)
              .message(e.getMessage() == null ? "not unavailable" : e.getMessage())
              .token(EMPTY_TOKEN).build())).build();
    }
    
  }
}

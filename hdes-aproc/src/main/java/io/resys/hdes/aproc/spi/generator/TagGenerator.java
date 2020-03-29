package io.resys.hdes.aproc.spi.generator;

/*-
 * #%L
 * hdes-aproc
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.aproc.spi.HdesAnnotationProcessor;
import io.resys.hdes.execution.HdesService;
import io.resys.hdes.execution.HdesService.Executable;
import io.resys.hdes.execution.HdesService.Tag;
import io.resys.hdes.storage.api.Changes;

public class TagGenerator {
  
  public static Builder builder(Consumer<JavaFile> consumer) {
    return new Builder(consumer);
  }
  
  public static class Builder {
    private String id;    
    private String name;
    private StringBuilder hash = new StringBuilder();
    private List<Changes> tagValues;
    private final Consumer<JavaFile> consumer;
    private static final ParameterizedTypeName valuesType = ParameterizedTypeName.get(Map.class, String.class, Executable.class);

    public Builder(Consumer<JavaFile> consumer) {
      super();
      this.consumer = consumer;
    }
    
    public Builder id(String id) {
      this.id = id;
      return this;
    }
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    public Builder values(List<Changes> values) {
      this.tagValues = values;
      return this;
    }
    
    public JavaFile build() {
      CodeBlock.Builder executables = CodeBlock.builder().addStatement("$T values = new $T<>()", valuesType, HashMap.class);

      for(Changes change : tagValues) {
        JavaFile javaFile = ExecutableGenerator.builder(consumer).tagId(id).changes(change).build();
        executables.addStatement("$T $L = add(values, new $T())", 
            HdesService.Executable.class,
            javaFile.typeSpec.name,
            ClassName.get(javaFile.packageName, javaFile.typeSpec.name));
      }
      
      CodeBlock.Builder values = CodeBlock.builder()
          .add(executables.build())
          .addStatement("NAME = $S", name)
          .addStatement("HASH = $S", hash.toString())
          .addStatement("EXECUTABLES = $T.unmodifiableMap(values)", Collections.class);

      TypeSpec tags = TypeSpec.classBuilder("ImmutableTag_" + id)
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addSuperinterface(Tag.class)
          .addField(String.class, "NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .addField(String.class, "HASH", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .addField(valuesType, "EXECUTABLES", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .addStaticBlock(values.build())
          .addMethod(MethodSpec.methodBuilder("getName")
              .addModifiers(Modifier.PUBLIC)
              .returns(String.class)
              .addStatement("return NAME")
              .build())
          .addMethod(MethodSpec.methodBuilder("getHash")
              .addModifiers(Modifier.PUBLIC)
              .returns(String.class)
              .addStatement("return HASH")
              .build())
          .addMethod(MethodSpec.methodBuilder("get")
              .addModifiers(Modifier.PUBLIC)
              .returns(ParameterizedTypeName.get(Collection.class, HdesService.Executable.class))
              .addStatement("return EXECUTABLES.values()")
              .build())
          .addMethod(MethodSpec.methodBuilder("add")
              .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
              .returns(HdesService.Executable.class)
              .addParameter(valuesType, "values")
              .addParameter(Executable.class, "value")
              .addStatement("values.put(value.getLabel() + \"_\" + value.getName(), value)")
              .addStatement("return value")
              .build())
          .addMethod(MethodSpec.methodBuilder("dt")
              .addModifiers(Modifier.PUBLIC)
              .returns(ParameterizedTypeName.get(Optional.class, Executable.class))
              .addParameter(String.class, "name")
              .addStatement("return $T.ofNullable(EXECUTABLES.get(\"dt_\" + name))", Optional.class)
              .build())
          .addMethod(MethodSpec.methodBuilder("st")
              .addModifiers(Modifier.PUBLIC)
              .returns(ParameterizedTypeName.get(Optional.class, Executable.class))
              .addParameter(String.class, "name")
              .addStatement("return $T.ofNullable(EXECUTABLES.get(\"st_\" + name))", Optional.class)
              .build())
          .addMethod(MethodSpec.methodBuilder("flow")
              .addModifiers(Modifier.PUBLIC)
              .returns(ParameterizedTypeName.get(Optional.class, Executable.class))
              .addParameter(String.class, "name")
              .addStatement("return $T.ofNullable(EXECUTABLES.get(\"flow_\" + name))", Optional.class)
              .build())
          .build();
      
      JavaFile result = JavaFile.builder(HdesAnnotationProcessor.class.getPackage().getName() + ".tag", tags).build();
      consumer.accept(result);
      return result;
    }
  }
}

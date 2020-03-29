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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.aproc.spi.HdesAnnotationProcessor;
import io.resys.hdes.execution.HdesService.Tag;
import io.resys.hdes.execution.HdesService.Tags;

public class TagsGenerator {
  
  public static Builder builder(Consumer<JavaFile> consumer) {
    return new Builder(consumer);
  }
  
  public static class Builder {
    private static final ParameterizedTypeName valuesType = ParameterizedTypeName.get(Map.class, String.class, Tag.class);
    private final Consumer<JavaFile> consumer;
    private CodeBlock.Builder values = CodeBlock.builder()
        .addStatement("$T values = new $T<>()", valuesType, HashMap.class);

    public Builder(Consumer<JavaFile> consumer) {
      super();
      this.consumer = consumer;
    }
    
    public Builder add(JavaFile tag) {
      ClassName type = ClassName.get(tag.packageName, tag.typeSpec.name);
      values = values.addStatement("add(values, new $T())", type);
      return this;
    }
    
    public JavaFile build() {
      TypeSpec tags = TypeSpec.classBuilder("ImmutableTags")
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addSuperinterface(Tags.class)
          .addField(valuesType, "VALUES", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .addStaticBlock(values.addStatement("VALUES = $T.unmodifiableMap(values)", Collections.class).build())
          .addMethod(MethodSpec.methodBuilder("getValues")
              .addModifiers(Modifier.PUBLIC)
              .returns(valuesType)
              .addStatement("return VALUES")
              .build())
          .addMethod(MethodSpec.methodBuilder("add")
              .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
              .addParameter(valuesType, "values")
              .addParameter(Tag.class, "tag")
              .addStatement("values.put(tag.getName(), tag)")
              .build())
          .build();
      
      JavaFile result = JavaFile.builder(HdesAnnotationProcessor.class.getPackage().getName(), tags).build();
      consumer.accept(result);
      return result;
    }
  }
}

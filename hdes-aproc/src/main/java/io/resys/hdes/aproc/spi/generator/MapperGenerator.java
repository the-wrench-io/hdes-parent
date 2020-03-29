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

import java.util.function.Consumer;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.aproc.spi.HdesAnnotationProcessor;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.GenericDataTypeService;

public class MapperGenerator {
  public static ClassName TYPE_NAME = ClassName.get(HdesAnnotationProcessor.class.getPackage().getName() + ".mapper", "DataTypeMapper");
  
  public static Builder builder(Consumer<JavaFile> consumer) {
    return new Builder(consumer);
  }

  public static class Builder {
    private final Consumer<JavaFile> consumer;

    public Builder(Consumer<JavaFile> consumer) {
      super();
      this.consumer = consumer;
    }

    public JavaFile build() {
      CodeBlock.Builder values = CodeBlock.builder()
          .addStatement("VALUE = $T.config().build()", GenericDataTypeService.class);
      TypeSpec typeSpec = TypeSpec.classBuilder(TYPE_NAME)
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addField(DataTypeService.class, "VALUE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .addStaticBlock(values.build())
          .addMethod(MethodSpec.methodBuilder("get")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
              .returns(DataTypeService.class)
              .addStatement("return VALUE")
              .build())
          .build();
      JavaFile result = JavaFile.builder(TYPE_NAME.packageName(), typeSpec).build();
      consumer.accept(result);
      return result;
    }
  }
}

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

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.aproc.spi.HdesAnnotationProcessor;
import io.resys.hdes.aproc.spi.exceptions.GeneratorSourceNotSupported;
import io.resys.hdes.aproc.spi.model.ModelFactory;
import io.resys.hdes.decisiontable.spi.DecisionTableExpressions;
import io.resys.hdes.storage.api.Changes;

public class ExecutableGenerator {
  
  public static Builder builder(Consumer<JavaFile> consumer) {
    return new Builder(consumer);
  }

  public static class Builder {
    private final Consumer<JavaFile> consumer;
    private Changes changes;
    private String tagId;

    public Builder(Consumer<JavaFile> consumer) {
      super();
      this.consumer = consumer;
    }

    public Builder tagId(String tagId) {
      this.tagId = tagId;
      return this;
    }
    
    public Builder changes(Changes changes) {
      this.changes = changes;
      return this;
    }

    public JavaFile build() {
      ModelFactory modelFactory = ModelFactory.config().build();

      final JavaFile result;
      switch (changes.getLabel()) {
      case "dt": {
        TypeSpec typeSpec = ExecutableGeneratorDt.builder(modelFactory).tagId(tagId).changes(changes).build();
        result = JavaFile
            .builder(HdesAnnotationProcessor.class.getPackage().getName() + "." + tagId, typeSpec)
            .addStaticImport(DecisionTableExpressions.class, 
                "evalDecimal", "evalLong", "evalInteger", "evalBoolean", "evalDate", "evalDateTime", "evalString")
            .build();
        break;
      }
      case "flow": {
        TypeSpec typeSpec = ExecutableGeneratorFlow.builder(modelFactory).tagId(tagId).changes(changes).build();
        result = JavaFile
        .builder(HdesAnnotationProcessor.class.getPackage().getName() + "." + tagId, typeSpec)
        .build();
        break;
      }
      default:
        throw GeneratorSourceNotSupported.builder().label(changes.getLabel()).build();
      }
      
      consumer.accept(result);
      return result;
    }
  }
}

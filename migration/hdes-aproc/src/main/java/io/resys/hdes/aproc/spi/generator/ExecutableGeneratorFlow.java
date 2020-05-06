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

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.aproc.spi.generator.flow.builders.FlowExecutionBuilder;
import io.resys.hdes.aproc.spi.generator.flow.builders.FlowExecutionBuilderDelegate;
import io.resys.hdes.aproc.spi.generator.flow.builders.FlowExecutionBuilderGetters;
import io.resys.hdes.aproc.spi.generator.flow.builders.FlowExecutionBuilderRun;
import io.resys.hdes.aproc.spi.generator.flow.builders.FlowExecutionBuilderSuccess;
import io.resys.hdes.aproc.spi.model.ModelFactory;
import io.resys.hdes.execution.HdesService.Executable;
import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.storage.api.Changes;

public class ExecutableGeneratorFlow {
  public static Builder builder(ModelFactory modelFactory) {
    return new Builder(modelFactory);
  }

  public static class Builder {
    private final ModelFactory modelFactory;
    private Changes changes;
    private String tagId;

    public Builder(ModelFactory modelFactory) {
      super();
      this.modelFactory = modelFactory;
    }

    public Builder tagId(String tagId) {
      this.tagId = tagId;
      return this;
    }

    public Builder changes(Changes changes) {
      this.changes = changes;
      return this;
    }

    public TypeSpec build() {
      FlowModel.Root modelRoot = modelFactory.flow().src(changes.getValues()).build();
      FlowAst ast = modelFactory.flowAst().from(modelRoot).build();

      TypeSpec.Builder result = TypeSpec.classBuilder(NameFactory.executable().label(changes.getLabel()).tagId(tagId).model(ast.getId()).build())
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addSuperinterface(Executable.class);

      FlowExecutionBuilder executionBuilder = FlowExecutionBuilderDelegate.from(
          new FlowExecutionBuilderGetters(), 
          new FlowExecutionBuilderRun(),
          new FlowExecutionBuilderSuccess()
          )
          .changes(changes)
          .tag(tagId)
          .model(modelRoot)
          .ast(ast);

      addTask(ast.getTask(), executionBuilder);
      executionBuilder.build(result);
      return result.build();
    }
  }
  
  private static void addTask(FlowAst.Task task, FlowExecutionBuilder executionBuilder) {
    executionBuilder.task(task);
    for(FlowAst.Task next : task.getNext()) {
      addTask(next, executionBuilder);
    }
  }
}

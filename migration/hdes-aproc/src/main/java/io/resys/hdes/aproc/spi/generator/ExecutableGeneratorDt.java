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

import io.resys.hdes.aproc.spi.generator.dt.builders.DTExecutionBuilder;
import io.resys.hdes.aproc.spi.generator.dt.builders.DTExecutionBuilderDelegate;
import io.resys.hdes.aproc.spi.generator.dt.builders.DTExecutionBuilderGetters;
import io.resys.hdes.aproc.spi.generator.dt.builders.DTExecutionBuilderRun;
import io.resys.hdes.aproc.spi.generator.dt.builders.DTExecutionBuilderSuccess;
import io.resys.hdes.aproc.spi.model.ModelFactory;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.execution.HdesService.Executable;
import io.resys.hdes.storage.api.Changes;

public class ExecutableGeneratorDt {
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
      DecisionTableModel model = modelFactory.dt().src(changes.getValues()).build();
      TypeSpec.Builder result = TypeSpec.classBuilder(NameFactory.executable().label(changes.getLabel()).tagId(tagId).model(model.getName()).build())
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addSuperinterface(Executable.class);
      
      DTExecutionBuilder executionBuilder = DTExecutionBuilderDelegate.from(
          new DTExecutionBuilderGetters(), 
          new DTExecutionBuilderRun(),
          new DTExecutionBuilderSuccess())
          .changes(changes)
          .tag(tagId)
          .model(model);
      
      model.getHeaders().forEach(header -> executionBuilder.addHeader(header));
      model.getRows().forEach(row -> {
        executionBuilder.addRow(row);
        
        int cellIndex = 0;
        for(Cell cell : row.getCells()) {
          Header header = model.getHeaders().get(cellIndex);
          executionBuilder.addCell(row, cell, header);
          cellIndex++;
        }
      });
      
      executionBuilder.build(result);
      return result.build();
    }
  }
}

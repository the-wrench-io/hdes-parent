package io.resys.hdes.decisiontable.spi.ast;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableFlatModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableService;

public class DelegateDecisionTableAstBuilder implements DecisionTableService.AstBuilder {

  private final DataTypeService dataTypeService;

  private DecisionTableModel normalModel;
  private DecisionTableFlatModel flatModel;

  public DelegateDecisionTableAstBuilder(DataTypeService dataTypeService) {
    this.dataTypeService = dataTypeService;
  }

  @Override
  public DecisionTableService.AstBuilder from(DecisionTableModel model) {
    this.normalModel = model;
    return this;
  }

  @Override
  public DecisionTableService.AstBuilder from(DecisionTableFlatModel model) {
    this.flatModel = model;
    return this;
  }

  @Override
  public DecisionTableAst build() {
    Assert.isTrue(flatModel != null || normalModel != null, () -> "model can't be null");

    if(flatModel != null) {
      return new FlatDecisionTableAstBuilder(dataTypeService).from(flatModel).build();
    }
    return new CommandDecisionTableAstBuilder(dataTypeService).from(normalModel).build();
  }
}

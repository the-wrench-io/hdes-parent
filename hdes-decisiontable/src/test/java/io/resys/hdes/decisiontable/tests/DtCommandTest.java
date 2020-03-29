package io.resys.hdes.decisiontable.tests;


import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableFlatModel;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.tests.config.TestDtConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/*-
 * #%L
 * wrench-component-assets-Dt
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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


public class DtCommandTest {

  private DecisionTableService decisionTableRepository = TestDtConfig.decisionTableRepository;

  @Test
  public void commandFormatExport() throws IOException {
    DecisionTableFlatModel flatDecisionTable = TestDtConfig.dataTypeService.read().classpath("dt.json").build(DecisionTableFlatModel.class);
    DecisionTableAst ast = decisionTableRepository.ast().from(flatDecisionTable).build();

    Assertions.assertEquals("", "");
  }

}

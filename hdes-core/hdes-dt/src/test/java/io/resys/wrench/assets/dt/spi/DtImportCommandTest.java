package io.resys.wrench.assets.dt.spi;

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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.hdes.client.api.model.DecisionTable;
import io.resys.wrench.assets.datatype.spi.util.FileUtils;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.spi.config.TestDtConfig;


@RunWith(BlockJUnit4ClassRunner.class)
public class DtImportCommandTest {

  private DecisionTableRepository decisionTableRepository = TestDtConfig.decisionTableRepository();

  @Test
  public void commandFormatExport() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "dt-import.json");
    DecisionTable decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

  }

}

package io.resys.wrench.assets.flow.spi;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.resys.wrench.assets.datatype.spi.util.FileUtils;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.api.model.FlowAst;
import io.resys.wrench.assets.flow.spi.config.TestFlowConfig;


@RunWith(BlockJUnit4ClassRunner.class)
public class TestFlowReader {
  
  private ObjectMapper objectMapper = TestFlowConfig.objectMapper();
  private FlowRepository repository = TestFlowConfig.flowRepository();

  @Test
  public void assets() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "trafficMain.in.json");
    String content = objectMapper.readValue(stream, ObjectNode.class).get("content").asText();

    ArrayNode commands = objectMapper.readValue(content, ArrayNode.class);
    FlowAst flowCommandModel = repository.createNode().src(commands).build();

    String expected = FileUtils.toString(getClass(), "trafficMain.out.yaml");
    Assert.assertEquals(expected, flowCommandModel.getSrc().getValue());
  }

}

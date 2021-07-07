package io.resys.wrench.assets.bundle.spi;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

/*-
 * #%L
 * wrench-assets-bundle
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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.resys.wrench.assets.bundle.spi.exporters.AssetToFileExporter;
import io.resys.wrench.assets.bundle.spi.exporters.FlowFlatToCommandExporter;
import io.resys.wrench.assets.bundle.spi.exporters.FlowTaskFlatToCommandExporter;
import io.resys.wrench.assets.bundle.spi.exporters.NameToGIDExporter;

@RunWith(SpringRunner.class)
public class ExportTest {
  
  @Ignore
  @Test
  public void export() {
    AssetToFileExporter.create().resource("classpath:export/test.json").build();
  }

  @Ignore
  @Test
  public void convertFlow() {
    new FlowFlatToCommandExporter().build("src/test/resources");
  }

  @Ignore
  @Test
  public void convertFlowTask() {
    new FlowTaskFlatToCommandExporter().build("src/test/resources");
  }

  @Ignore
  @Test
  public void convertNames() {
    new NameToGIDExporter().build("src/main/resources");
  }


  @Ignore
  @Test
  public void forUploadDialob() {
    String fileIn = "6d5acf7d75c9d9a6ebd8c50ef8509502.json";
    String fileOut = "target/output.json";
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
    try {
      Resource resource = context.getResource("classpath:" + fileIn);

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode content = objectMapper.readTree(resource.getInputStream());

      ObjectNode asset = objectMapper.createObjectNode();
      asset.set("type", new TextNode("DIALOB"));
      asset.set("content", new TextNode(content.toString()));


      FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
      IOUtils.copy(new ByteArrayInputStream(asset.toString().getBytes(StandardCharsets.UTF_8)), fileOutputStream);
      fileOutputStream.close();


    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      context.close();
    }
  }

}

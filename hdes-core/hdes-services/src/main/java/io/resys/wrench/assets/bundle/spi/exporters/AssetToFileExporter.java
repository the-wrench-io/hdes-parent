package io.resys.wrench.assets.bundle.spi.exporters;

/*-
 * #%L
 * wrench-component-assets
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.datatype.spi.util.FileUtils;

public class AssetToFileExporter {
  private final ObjectMapper objectMapper;
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

  private String location;

  private AssetToFileExporter() {
    objectMapper = new ObjectMapper();
  }

  /**
   * @param patternForSpring "classpath:" + location
   */
  public AssetToFileExporter resource(String location) {
    this.location = location;
    return this;
  }

  public void build() {
    try {
      String contents = getFileContents(location);
      ArrayNode assets = (ArrayNode) objectMapper.readTree(contents);
      assets.forEach(node -> write(node, "src/main/resources/assets/"));
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  public void build(String output) {
    try {
      String contents = getFileContents(location);
      ArrayNode assets = (ArrayNode) objectMapper.readTree(contents);
      assets.forEach(node -> write(node, output));
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  protected void write(JsonNode node, String output) {
    try {
      ServiceType type = ServiceType.valueOf(node.get("type").asText());
      String name = getResourceName(type, node.get("name").asText());

      File result = new File(FileUtils.cleanPath(output) + "/" + name);
      if(!result.exists()) {
        result.getParentFile().mkdirs();
        result.createNewFile();
      }

      // copy data to file
      FileOutputStream fileOutputStream = new FileOutputStream(result);
      IOUtils.copy(new ByteArrayInputStream(getSrc(type, node).getBytes(StandardCharsets.UTF_8)), fileOutputStream);
      fileOutputStream.close();

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  protected String getResourceName(ServiceType type, String name) {
    if(type == ServiceType.FLOW) {
      return "flow/" + name + ".yaml";
    } else if (type == ServiceType.DT) {
      return "dt/" + name + ".json";
    } else if (type == ServiceType.FLOW_TASK) {
      return "flowtask/" + name + ".groovy";
    } else if (type == ServiceType.TAG) {
      return "tag/" + name + ".json";
    } else if (type == ServiceType.DATA_TYPE) {
      return "datatype/" + name + ".yaml";
    }
    throw new IllegalArgumentException("Unknown service type: " + type + "!");
  }

  protected String getSrc(ServiceType type, JsonNode node) {
    String wrappedSrc = node.get("content").asText();
    return wrappedSrc;
  }

  public static AssetToFileExporter create() {
    return new AssetToFileExporter();
  }

  public String getFileContents(String location) {
    try {
      return IOUtils.toString(resolver.getResource(location).getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}

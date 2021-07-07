package io.resys.wrench.assets.bundle.spi.exporters;

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

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.resys.wrench.assets.flow.spi.support.FlowFlatToCommandBuilder;

public class FlowFlatToCommandExporter {
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  
  public void build() {
    build("src/main/resources", null);
  }

  public void build(String location) {
    build(location, null);
  }

  public void build(String location, String name) {
    FlowFlatToCommandBuilder commandBuilder = new FlowFlatToCommandBuilder(objectMapper());

    try {
      for(Resource resource : resolver.getResources("classpath*:assets/flow/**")) {

        if(name != null && !resource.getFilename().equals(name)) {
          continue;
        }
        String result = commandBuilder.build(resource.getInputStream());

        String fileName = location + "/assets/flow/" + resource.getFilename();
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        IOUtils.copy(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)), fileOutputStream);
        fileOutputStream.close();
      }

    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    JsonDeserializer<LocalDateTime> localDateTime = new JsonDeserializer<LocalDateTime>() {
      @Override
      public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        return LocalDateTime.ofInstant(ZonedDateTime.parse(node.asText()).toInstant(), ZoneId.systemDefault());
      }
    };
    JsonSerializer<LocalDate> localDate = new JsonSerializer<LocalDate>() {
      @Override
      public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
      }
    };
    SimpleModule jsonDeserializerModule = new SimpleModule();
    jsonDeserializerModule.addDeserializer(LocalDateTime.class, localDateTime);
    jsonDeserializerModule.addSerializer(LocalDate.class, localDate);
    objectMapper.registerModule(jsonDeserializerModule);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}

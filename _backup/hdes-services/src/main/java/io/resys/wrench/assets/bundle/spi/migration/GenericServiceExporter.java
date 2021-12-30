package io.resys.wrench.assets.bundle.spi.migration;

/*-
 * #%L
 * hdes-services
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Migration;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.MigrationBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.MigrationValue;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableMigration;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableMigrationValue;



public class GenericServiceExporter implements MigrationBuilder {

  private final AssetServiceRepository serviceRepository;
  private final ObjectMapper objectMapper;

  public GenericServiceExporter(AssetServiceRepository serviceRepository, ObjectMapper objectMapper) {
    super();
    this.serviceRepository = serviceRepository;
    this.objectMapper = objectMapper;
  }

  private static String md5(String ...input) {
    Vector<InputStream> v = new Vector<>();
    
    for(final var el : input) {
      v.add(new ByteArrayInputStream(el.getBytes(StandardCharsets.UTF_8)));
    }
    SequenceInputStream seqStream = new SequenceInputStream(v.elements());
    try {
      String md5Hash = DigestUtils.md5Hex(seqStream);
      seqStream.close();
      return md5Hash;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private MigrationValue visitDt(AssetService service) throws IOException {
    final var dt = serviceRepository.getTypes().ast().commands(service.getSrc()).decision();

    return ImmutableMigrationValue.builder()
        .id(md5(service.getSrc()))
        .type(ServiceType.DT)
        .name(service.getName())
        .addAllCommands(objectMapper.readValue(dt.getSource(), new TypeReference<List<AstCommand>>() {}))
        .build();
  }

  private MigrationValue visitSt(AssetService service) throws IOException {
    final var st = serviceRepository.getTypes().ast().commands(service.getSrc()).service();
  
    return ImmutableMigrationValue.builder()
        .id(md5(service.getSrc()))
        .type(ServiceType.FLOW_TASK)
        .name(service.getName())
        .addAllCommands(objectMapper.readValue(st.getSource(), new TypeReference<List<AstCommand>>() {}))
        .build();
  }

  private MigrationValue visitFl(AssetService service) throws IOException {
    final var builder = ImmutableMigrationValue.builder().name(service.getName()).id(md5(service.getSrc())).type(ServiceType.FLOW);
    
    AstFlow commandModel  = serviceRepository.getTypes().ast()
        .commands((ArrayNode) objectMapper.readTree(service.getSrc()))
        .flow();
    BufferedReader br = new BufferedReader(new StringReader(commandModel.getSrc().getValue()));
    try {
      String line;
      int index = 0;
      while ((line = br.readLine()) != null) {
        final var command = ImmutableAstCommand.builder().id(String.valueOf(index++)).value(line)
            .type(AstCommandValue.ADD).build();
        builder.addCommands(command);
      }
    } finally {
      br.close();
    }
    return builder.build();
  }

  @Override
  public Migration build() {
    final var result = ImmutableMigration.builder();
    final var inputs = new ArrayList<String>();
    for (final var service : serviceRepository.createQuery().list()) {
      try {
        switch (service.getType()) {
        case FLOW:
          inputs.add(service.getSrc());
          result.addValue(visitFl(service));
          break;
        case DT:
          inputs.add(service.getSrc());
          result.addValue(visitDt(service));
          break;
        case FLOW_TASK:
          inputs.add(service.getSrc());
          result.addValue(visitSt(service));
          break;

        default:
          continue;
        }
      } catch (Exception e) {
        throw new RuntimeException("Failed to create migration because of asset: '"  + service.getId() + "::" + service.getName() + "', msg: " + e.getMessage(), e);
      }
    }
    return result.id(md5(inputs.toArray(new String[0]))).build();
  }
}
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
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Migration;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.MigrationBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.MigrationValue;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableMigration;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableMigrationValue;
import io.resys.wrench.assets.datatype.api.ImmutableAstCommandType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.api.model.DecisionTable;
import io.resys.wrench.assets.dt.spi.export.CommandModelDecisionTableExporter;
import io.resys.wrench.assets.flow.api.model.FlowAst;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandType;
import io.resys.wrench.assets.script.api.ScriptRepository.Script;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommandType;



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

  private MigrationValue visitDt(Service service) {
    DecisionTable dt = serviceRepository.getDtRepo().createBuilder().format(DecisionTableFormat.JSON)
        .src(service.getSrc()).build();
    final var exporter = (CommandModelDecisionTableExporter) new CommandModelDecisionTableExporter(objectMapper)
        .src(dt);

    return ImmutableMigrationValue.builder().type(ServiceType.DT).name(service.getName())
        .id(md5(service.getSrc()))
        .addAllCommands(exporter.buildCommands()).build();
  }

  private MigrationValue visitSt(Service service) throws IOException {
    final var builder = ImmutableMigrationValue.builder().id(md5(service.getSrc())).name(service.getName()).type(ServiceType.FLOW_TASK);
    Script commandModel  = serviceRepository.getStRepo().createBuilder().src(service.getSrc()).build();
    BufferedReader br = new BufferedReader(new StringReader(commandModel.getModel().getSrc()));
    try {
      String line;
      int index = 0;
      while ((line = br.readLine()) != null) {
        final var command = ImmutableAstCommandType.builder().id(String.valueOf(index++)).value(line)
            .type(ScriptCommandType.ADD.name()).build();
        builder.addCommands(command);
      }
    } finally {
      br.close();
    }
    return builder.build();
  }

  private MigrationValue visitFl(Service service) throws IOException {
    final var builder = ImmutableMigrationValue.builder().name(service.getName()).id(md5(service.getSrc())).type(ServiceType.FLOW);
    
    FlowAst commandModel  = serviceRepository.getFlRepo().createNode()
        .src((ArrayNode) objectMapper.readTree(service.getSrc()))
        .build();
    BufferedReader br = new BufferedReader(new StringReader(commandModel.getSrc().getValue()));
    try {
      String line;
      int index = 0;
      while ((line = br.readLine()) != null) {
        final var command = ImmutableAstCommandType.builder().id(String.valueOf(index++)).value(line)
            .type(FlowCommandType.ADD.name()).build();
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

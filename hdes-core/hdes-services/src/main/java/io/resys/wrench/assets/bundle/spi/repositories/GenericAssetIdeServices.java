package io.resys.wrench.assets.bundle.spi.repositories;

/*-
 * #%L
 * wrench-assets-ide-services
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.FlowAstType;
import io.resys.hdes.client.api.execution.Flow;
import io.resys.hdes.client.api.execution.Flow.FlowContext;
import io.resys.hdes.client.api.execution.Flow.FlowTask;
import io.resys.hdes.client.api.model.FlowModel;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskModel;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskType;
import io.resys.wrench.assets.bundle.api.repositories.AssetIdeServices;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Migration;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceQuery;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableAssetError;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableAssetResource;
import io.resys.wrench.assets.bundle.api.repositories.ImmutableAssetSummary;
import io.resys.wrench.assets.bundle.spi.dt.resolvers.DebugDtInputResolver;
import io.resys.wrench.assets.bundle.spi.exceptions.DataException;
import io.resys.wrench.assets.bundle.spi.flow.executors.TransientFlowExecutor;
import io.resys.wrench.assets.bundle.spi.flowtask.FlowTaskInput;
import io.resys.wrench.assets.script.api.ScriptRepository.Script;

public class GenericAssetIdeServices implements AssetIdeServices {
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  private final AssetServiceRepository assetServiceRepository;
  private final ObjectNode summary;
  private final TransientFlowExecutor transientFlowExecutor;
  private final ObjectMapper objectMapper;
  private final CsvMapper csvMapper = new CsvMapper();
  
  public GenericAssetIdeServices(ObjectMapper objectMapper,
      TransientFlowExecutor transientFlowExecutor,
      AssetServiceRepository assetServiceRepository,
      String uiHash) {
    super();
    this.objectMapper = objectMapper;
    this.assetServiceRepository = assetServiceRepository;
    this.transientFlowExecutor = transientFlowExecutor;
    
    ObjectNode summary = null;
    try {
      JsonNode node = objectMapper.readTree(resolver.getResource("classpath:buildinfo.git.properties").getInputStream());
      if(node != null) {
        ObjectNode output = objectMapper.createObjectNode();
        output.set("uiHash", new TextNode(uiHash));
        output.set("branch", node.get("git.branch"));
        output.set("buildTime", node.get("git.build.time"));
        output.set("version", node.get("git.build.version"));
        output.set("commit", node.get("git.commit.id.abbrev"));
        output.set("commitTime", node.get("git.commit.time"));
        output.set("deploymentHash", new TextNode(assetServiceRepository.getHash()));
        summary = output;
      }
    } catch(IOException e) {
    }
    
    if(summary == null) {
      summary = objectMapper.createObjectNode();
    }
    this.summary = summary;
  }
  @Override
  public AssetSummary summary() {
    return ImmutableAssetSummary.builder()
        .output(summary)
        .currentHash(assetServiceRepository.getHash())
        .build();
  }

  @Override
  public Map<ServiceType, List<ServiceDataModel>> models() {
    Map<ServiceType, List<ServiceDataModel>> models = new HashMap<>();
    assetServiceRepository.createQuery().list().forEach(s -> {
      if (!models.containsKey(s.getType())) {
        models.put(s.getType(), new ArrayList<>());
      }
      models.get(s.getType()).add(s.getDataModel());
    });
    return models;
  }

  @Override
  public String debug(AssetDebug entity) {
    Assert.isTrue(entity.getType() != null, () -> "AssetResource type can't be null!");
    
    final Service service;
    if(entity.getContent() == null) {
      service = assetServiceRepository.createQuery().id(entity.getId()).get().get();
    } else {
      service = assetServiceRepository.createBuilder(entity.getType()).
          name("debug").
          id(String.valueOf(Long.MIN_VALUE)).
          src(entity.getContent()).
          build();  
    }
    

    try {
       if(service.getType() == ServiceType.FLOW_TASK) { 
         Object outputs = service.newExecution()
             .insert(new FlowTaskInput(objectMapper.readValue(entity.getInput(), HashMap.class)))
             .run()
             .getDebug();
         
           return objectMapper.writeValueAsString(outputs);
         
       } else if(service.getType() == ServiceType.DT) {
         Object outputs = service.newExecution()
          .insert(new DebugDtInputResolver(objectMapper.readValue(entity.getInput(), HashMap.class)))
          .run()
          .getDebug();
        
        return objectMapper.writeValueAsString(outputs);

      } else if(service.getType() == ServiceType.FLOW) {

        if(entity.getInput() != null) {
          Map.Entry<Flow, ObjectNode> result = transientFlowExecutor.debug(service, objectMapper.readTree(entity.getInput()));
          Map<String, Object> output = new HashMap<>();
          
          output.put("debug", result.getKey());
          output.put("result", result.getValue());
          return objectMapper.writeValueAsString(output);
        } else if(entity.getInputCsv() != null) {
          
          
          CSVParser parser = CSVParser.parse(entity.getInputCsv(), CSVFormat.DEFAULT.withDelimiter(';').withIgnoreEmptyLines());
          List<CSVRecord> records = parser.getRecords();
          if(records.isEmpty()) {
            return null;
          }
          
          // header row
          Iterator<CSVRecord> iterator = records.iterator();
          Map<Integer, String> headers = new HashMap<>();
          int headerIndex = 0;
          for(String header : iterator.next()) {
            headers.put(headerIndex++, header);
          }
        
          final Set<String> fieldNames = new HashSet<>();
          
          // body
          ArrayNode arrayNode = objectMapper.createArrayNode();
          while(iterator.hasNext()) {
            CSVRecord row = iterator.next();
            JsonNode transactionId = objectMapper.convertValue(row.getRecordNumber(), JsonNode.class);
            JsonNode transaction;
            
            try {
              Flow flow = executeCsvRecord(headers, row, service);
              transaction = getLastTask(flow);
            } catch(DataException e) {
              transaction = objectMapper.createObjectNode();
              ((ObjectNode)transaction).set("_errors", objectMapper.convertValue(e.getMessagesList().get(), JsonNode.class));
            } catch(Exception e) {
              transaction = objectMapper.createObjectNode();
              ((ObjectNode)transaction).set("_errors", TextNode.valueOf(e.getMessage()));
            }
            
            if(transaction.isArray()) {
              for(JsonNode entry : (ArrayNode) transaction) {
                ObjectNode flatJson = createFlatJson(fieldNames, transactionId, (ObjectNode) entry);
                arrayNode.add(flatJson);
              }
            } else {
              ObjectNode flatJson = createFlatJson(fieldNames, transactionId, (ObjectNode) transaction);
              arrayNode.add(flatJson);
            }
            
          }
          
          // create schema
          CsvSchema.Builder schema = CsvSchema.builder();
          schema.addColumn("_id");
          fieldNames.forEach(name -> schema.addColumn(name));
          schema.addColumn("_errors");
          
          
          String csv = csvMapper.writer(schema.build().withHeader()).writeValueAsString(arrayNode);
          
          Map<String, String> outputs = new HashMap<>();
          outputs.put("csv", csv);
          return objectMapper.writeValueAsString(outputs);
          
        }
      }
      return null;
    } catch (IOException e) {
      throw new DataException(422, e);
    }
  }
  
  private ObjectNode createFlatJson(Set<String> fieldNames, JsonNode transactionId, ObjectNode transaction) {
    // collect column names
    ObjectNode flatObject = objectMapper.createObjectNode();
    flatObject.set("_id", transactionId);
    
    Iterator<Map.Entry<String, JsonNode>> fields = transaction.fields();
    while(fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      String fieldName = field.getKey();
      fieldNames.add(fieldName);
      
      JsonNode value;
      if(field.getValue().isArray() || field.getValue().isObject()) {
        value = TextNode.valueOf(field.getValue().toPrettyString());
      } else {
        value = field.getValue();
      }
      
      flatObject.set(fieldName, value);
    }
    return flatObject;
  }
  
  
  private JsonNode getLastTask(Flow flow) {
    FlowContext context = flow.getContext();
    FlowModel model = flow.getModel();
    Object output = null;
    for(FlowTask task : context.getTasks()) {
      if(isTaskPartOfOutput(task, model)) {
        String modelId = task.getModelId();
        output = task.getVariables().get(modelId);
      }
    }
    
    if(output == null) {
      output = new HashMap<>();
    }
    JsonNode jsonNode = objectMapper.convertValue(output, JsonNode.class);
    List<String> actualList = new ArrayList<String>();
    jsonNode.fieldNames().forEachRemaining(actualList::add);
    if(actualList.size() == 1) {
      JsonNode firstField = jsonNode.fields().next().getValue();
      if(firstField.isArray()) {
        return firstField;
      }
    }
    return jsonNode;
  }
  
  private boolean isTaskPartOfOutput(FlowTask task, FlowModel model) {
    for(FlowTaskModel taskModel : model.getTasks()) {
      if(!taskModel.getId().equals(task.getModelId())) {
        continue;
      }
      FlowTaskType taskType = taskModel.getType();
      return taskType == FlowTaskType.DT || taskType == FlowTaskType.SERVICE;
    }
    
    return false;
  }
  
  private Flow executeCsvRecord(Map<Integer, String> headers, CSVRecord row, Service service) throws JsonProcessingException {
    ObjectNode inputEntity = objectMapper.createObjectNode();
    int columnIndex = 0;
    for(String columnValue : row) {
      String columnName = headers.get(columnIndex++);
      try {
        inputEntity.set(columnName, objectMapper.readValue(columnValue, JsonNode.class));
      } catch(JsonProcessingException e) {
        inputEntity.set(columnName, objectMapper.convertValue(columnValue, JsonNode.class));
      }
    }
    Map.Entry<Flow, ObjectNode> result = transientFlowExecutor.execute(service, inputEntity);
    return result.getKey();
  }
  

  @Override
  public AssetResourceQuery query() {
    ServiceQuery query = assetServiceRepository.createQuery();
    return new AssetResourceQuery() {
      @Override
      public AssetResourceQuery type(ServiceType type) {
        query.type(type);
        return this;
      }
      @Override
      public AssetResourceQuery rev(String rev) {
        query.rev(rev);
        return this;
      }
      @Override
      public AssetResourceQuery name(String name) {
        query.name(name);
        return this;
      }
      @Override
      public AssetResourceQuery id(String id) {
        query.id(id);
        return this;
      }
      @Override
      public Collection<AssetResource> build() {
        return query.list().stream().map(service -> ImmutableAssetResource.builder()
              .id(service.getId())
              .name(service.getName())
              .type(service.getType())
              .content(service.getSrc())
              .addAllErrors(service.getDataModel().getErrors()
                  .stream().map(error -> ImmutableAssetError.builder().id(error.getId()).message(error.getMessage()).build())
                  .collect(Collectors.toList()))
              .build())
            .collect(Collectors.toList());
        }
    };
  }

  @Override
  public JsonNode commands(AssetCommand command) {
    switch(command.getType()) {
    case DT:
      return createDtCommands(command);
    case FLOW:
      return createFlowCommands(command);
    case FLOW_TASK:
      return createFlowTaskCommands(command);
    default: return null;
    }
  }

  protected JsonNode createDtCommands(AssetCommand command) {
    Assert.isTrue(command.getInput() == null || command.getInput().isArray(), "command input must be array!");

    DecisionAstType commandModel  = assetServiceRepository.getDtRepo().createCommandModelBuilder()
        .src(command.getInput())
        .rev(command.getRev())
        .build();

    JsonNode output = objectMapper.convertValue(commandModel, JsonNode.class);
    return output;
  }

  protected JsonNode createFlowCommands(AssetCommand command) {
    Assert.isTrue(command.getInput() == null || command.getInput().isArray(), "command input must be array!");

    FlowAstType commandModel  = assetServiceRepository.getFlRepo().createNode()
        .src((ArrayNode) command.getInput())
        .rev(command.getRev())
        .build();

    JsonNode output = objectMapper.convertValue(commandModel, JsonNode.class);
    return output;
  }

  protected JsonNode createFlowTaskCommands(AssetCommand command) {
    Assert.isTrue(command.getInput() == null || command.getInput().isArray(), "command input must be array!");

    Script commandModel  = assetServiceRepository.getStRepo().createBuilder()
        .src(command.getInput())
        .rev(command.getRev())
        .build();

    JsonNode output = objectMapper.convertValue(commandModel.getModel(), JsonNode.class);
    return output;
  }

  @Override
  public synchronized AssetResource persist(AssetResource asset) {
    // Create
    Service service = assetServiceRepository.createStore().save(assetServiceRepository.createBuilder(asset.getType())
        .id(asset.getId())
        .src(asset.getContent())
        .name(asset.getName())
        .build());

    
    // Map chains back to rest
    return ImmutableAssetResource.builder()
      .id(service.getId())
      .name(service.getName())
      .type(service.getType())
      .content(service.getSrc())
      .addAllErrors(service.getDataModel().getErrors()
          .stream().map(error -> ImmutableAssetError.builder().id(error.getId()).message(error.getMessage()).build())
          .collect(Collectors.toList()))
      .build();
  }
  
  @Override
  public synchronized AssetResource remove(AssetResource asset) {
    assetServiceRepository.createStore().remove(asset.getId());
    return asset;
  }

  @Override
  public AssetResource copyAs(AssetCopyAs copyAs) {
    final Service original = assetServiceRepository.createStore().get(copyAs.getId());
    
    
    // Create
    Service service = assetServiceRepository.createStore().save(assetServiceRepository.createBuilder(original.getType())
        .src(original.getSrc())
        .name(copyAs.getName())
        .rename()
        .build());
    
    // Map chains back to rest
    return ImmutableAssetResource.builder()
      .id(service.getId())
      .name(service.getName())
      .type(service.getType())
      .content(service.getSrc())
      .addAllErrors(service.getDataModel().getErrors()
          .stream().map(error -> ImmutableAssetError.builder().id(error.getId()).message(error.getMessage()).build())
          .collect(Collectors.toList()))
      .build();
  }
  @Override
  public Migration migrate() {
    return assetServiceRepository.createMigration().build();
  }

}

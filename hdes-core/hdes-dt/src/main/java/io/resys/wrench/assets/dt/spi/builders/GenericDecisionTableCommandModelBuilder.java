package io.resys.wrench.assets.dt.spi.builders;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.ast.AstType.AstCommandType;
import io.resys.hdes.client.api.ast.AstType.AstCommandType.AstCommandValue;
import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.DecisionAstType.ColumnExpressionType;
import io.resys.hdes.client.api.ast.DecisionAstType.HitPolicy;
import io.resys.hdes.client.spi.util.Assert;
import io.resys.hdes.client.api.ast.ImmutableAstCommandType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableCommandModelBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DynamicValueExpressionExecutor;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableCommandModelException;

public class GenericDecisionTableCommandModelBuilder implements DecisionTableCommandModelBuilder {

  private final static List<String> knownCommandTypes = Arrays.asList(AstCommandValue.values()).stream().map(c -> c.name()).collect(Collectors.toList());
  private final Supplier<List<String>> headerTypes;
  private final Supplier<DecisionTableExpressionBuilder> expressionBuilder;
  private final Map<ValueType, List<String>> headerExpressions;
  private final Supplier<DynamicValueExpressionExecutor> dynamicValueExpressionExecutor;
  private List<AstCommandType> src;
  private Integer rev;

  public GenericDecisionTableCommandModelBuilder(
      Supplier<List<String>> headerTypes,
      Supplier<DecisionTableExpressionBuilder> expressionBuilder,
      Map<ValueType, List<String>> headerExpressions,
      Supplier<DynamicValueExpressionExecutor> dynamicValueExpressionExecutor) {
    super();
    this.headerTypes = headerTypes;
    this.expressionBuilder = expressionBuilder;
    this.headerExpressions = headerExpressions;
    this.dynamicValueExpressionExecutor = dynamicValueExpressionExecutor;
  }

  @Override
  public DecisionTableCommandModelBuilder src(List<AstCommandType> src) {
    this.src = src;
    return this;
  }

  @Override
  public DecisionTableCommandModelBuilder src(JsonNode src) {
    if(src == null) {
      return this;
    }
    Assert.isTrue(src.isArray(), () -> "src must be array node!");
    this.src = new ArrayList<>();
    for(JsonNode node : src) {
      final String type = getString(node, "type");
      if(knownCommandTypes.contains(type)) {
        this.src.add(ImmutableAstCommandType.builder().id(getString(node, "id")).value(getString(node, "value")).type(AstCommandValue.valueOf(type)).build());
      }
    }
    return this;
  }

  @Override
  public DecisionTableCommandModelBuilder rev(Integer rev) {
    this.rev = rev;
    return this;
  }

  @Override
  public DecisionAstType build() {
    List<AstCommandType> src = CollectionUtils.isEmpty(this.src) ? Collections.emptyList() : this.src;
    CommandMapper.Builder builder = CommandMapper.builder()
        .headerTypes(headerTypes.get())
        .headerExpressions(headerExpressions)
        .expressionBuilder(expressionBuilder)
        .dynamicValueExpressionExecutor(dynamicValueExpressionExecutor);

    if(this.rev != null) {
      int limit = this.rev;
      int runningVersion = 0;
      for(AstCommandType command : src) {
        if(runningVersion++ > limit) {
          break;
        }
        execute(builder, command);
      }
      builder.version(limit);
    } else {
      src.forEach(command -> execute(builder, command));
      builder.version(src.size());
    }

    return builder.build();
  }

  protected CommandMapper.Builder execute(CommandMapper.Builder builder, AstCommandType command) {
    try {
      final var type = command.getType();
      switch(type) {
      case SET_NAME:
        return builder.name(command.getValue());
      case SET_DESCRIPTION:
        return builder.description(command.getValue());
      case SET_HIT_POLICY:
        return builder.hitPolicy(!StringUtils.isEmpty(command.getValue()) ? HitPolicy.valueOf(command.getValue()) : null);
      case MOVE_ROW:
        // swap
        return builder.moveRow(command.getId(), command.getValue());
      case INSERT_ROW:
        // insert
        return builder.insertRow(command.getId(), command.getValue());
        
      case COPY_ROW:
        return builder.copyRow(command.getId());
      case MOVE_HEADER:
        return builder.moveHeader(command.getId(), command.getValue());
      case SET_HEADER_TYPE:
        return builder.changeHeaderType(command.getId(), command.getValue());
      case SET_HEADER_SCRIPT:
        return builder.changeHeaderScript(command.getId(), command.getValue());
      
      case SET_HEADER_REF:
        return builder.changeHeaderName(command.getId(), command.getValue());
      case SET_HEADER_DIRECTION:
        return builder.changeHeaderDirection(command.getId(), Direction.valueOf(command.getValue()));
      case SET_HEADER_EXPRESSION:
        return builder.setHeaderExpression(command.getId(), ColumnExpressionType.valueOf(command.getValue()));
      case SET_CELL_VALUE:
        return builder.changeCell(command.getId(), command.getValue());
      case DELETE_CELL:
        return builder.deleteCell(command.getId());
      case DELETE_HEADER:
        return builder.deleteHeader(command.getId());
      case DELETE_ROW:
        return builder.deleteRow(command.getId());
      case ADD_HEADER_IN:
        return builder.addHeader(Direction.IN, command.getId() != null ? command.getId() : "").getValue();
      case ADD_HEADER_OUT:
        return builder.addHeader(Direction.OUT, command.getId() != null ? command.getId() : "").getValue();
      case ADD_ROW:
        return builder.addRow().getValue();
      case IMPORT_ORDERED_CSV: {
        
        CommandMapper.Builder result = builder.deleteColumns().deleteRows();
        
        CSVParser parser = CSVParser.parse(command.getValue(), CSVFormat.DEFAULT);
        List<CSVRecord> records = parser.getRecords();
        if(records.isEmpty()) {
          return result;
        }
        
        Iterator<CSVRecord> iterator = records.iterator();
        CSVRecord csvHeader = iterator.next();
        csvHeader.forEach(c -> {
          String id = result.addHeader(Direction.IN, "").getKey();
          result.changeHeaderType(id, ValueType.STRING.name());
          result.changeHeaderName(id, c);
        });

        while(iterator.hasNext()) {
          CSVRecord row = iterator.next();
          String rowId = result.addRow().getKey();
          Iterator<String> cellIterator = row.iterator();
          
          int columnIndex = 0;
          while(cellIterator.hasNext()) {
            result.changeCell(rowId, columnIndex++, cellIterator.next());
          }
        }
        
        return result;
      }
      case IMPORT_CSV: 
        CommandMapper.Builder result = builder.deleteColumns().deleteRows();
        CSVParser parser = CSVParser.parse(command.getValue(), CSVFormat.DEFAULT);
        List<CSVRecord> records = parser.getRecords();
        if(records.isEmpty()) {
          return result;
        }
        
        Iterator<CSVRecord> iterator = records.iterator();
        CSVRecord csvHeader = iterator.next();
        csvHeader.forEach(c -> {
          String id = result.addHeader(Direction.IN, "").getKey();
          result.changeHeaderType(id, ValueType.STRING.name());
          result.changeHeaderName(id, c);
        });

        while(iterator.hasNext()) {
          CSVRecord row = iterator.next();
          int rowId = Integer.parseInt(result.addRow().getKey());
          Iterator<String> cellIterator = row.iterator();
          while(cellIterator.hasNext()) {
            result.changeCell(String.valueOf(++rowId), cellIterator.next());
          }
        }
        return result;
        
      default: return builder;
      }
    } catch(DecisionTableCommandModelException e) {
      throw e;
    } catch(Exception e) {
      throw new DecisionTableCommandModelException(command, e.getMessage(), e);
    }
  }

  protected String getString(JsonNode node, String name) {
    return node.hasNonNull(name) ? node.get(name).asText() : null;
  }

}

package io.resys.hdes.client.spi.decision.ast;

/*-
 * #%L
 * hdes-client-api
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


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.ast.AstDataType.Direction;
import io.resys.hdes.client.api.ast.AstDataType.ValueType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.DecisionAstType.ColumnExpressionType;
import io.resys.hdes.client.api.ast.DecisionAstType.Header;
import io.resys.hdes.client.api.ast.DecisionAstType.HitPolicy;
import io.resys.hdes.client.api.ast.DecisionAstType.Row;
import io.resys.hdes.client.api.ast.ImmutableCell;
import io.resys.hdes.client.api.ast.ImmutableDecisionAstType;
import io.resys.hdes.client.api.ast.ImmutableHeader;
import io.resys.hdes.client.api.ast.ImmutableRow;
import io.resys.hdes.client.api.exceptions.DecisionAstException;
import io.resys.hdes.client.api.execution.DecisionTableResult.DynamicValueExpressionExecutor;
import io.resys.hdes.client.api.execution.DecisionTableResult.Expression;
import io.resys.hdes.client.spi.decision.SpringDynamicValueExpressionExecutor;
import io.resys.hdes.client.spi.decision.execution.OperationFactory;
import io.resys.hdes.client.spi.util.Assert;



public class CommandMapper {
  private final static List<String> headerTypes;
  private final static Map<ValueType, List<String>> headerExpressions;
  
  static {
    headerExpressions = Collections.unmodifiableMap(Map.of(
        ValueType.INTEGER, Collections.unmodifiableList(Arrays.asList(ColumnExpressionType.EQUALS.name())),
        ValueType.DECIMAL, Collections.unmodifiableList(Arrays.asList(ColumnExpressionType.EQUALS.name())),
        ValueType.STRING, Collections.unmodifiableList(Arrays.asList(ColumnExpressionType.IN.name()))    
    ));
    headerTypes = Collections.unmodifiableList(
        Arrays.asList(ValueType.STRING,  ValueType.BOOLEAN, ValueType.INTEGER, ValueType.LONG, ValueType.DECIMAL, ValueType.DATE, ValueType.DATE_TIME).stream()
        .map(v -> v.name()).collect(Collectors.toList()));
  }
  
  
  public static Builder builder(ObjectMapper objectMapper) {
    return new Builder(objectMapper);
  }

  public static class Builder {
    private final ObjectMapper objectMapper;
    private final SpringDynamicValueExpressionExecutor dynamicValueExpressionExecutor = new SpringDynamicValueExpressionExecutor(); 
    private long idGen = 0;
    private String name;
    private String description;
    private HitPolicy hitPolicy;
    private int version;
    
    private final Map<String, MutableHeader> headers = new HashMap<>();
    private final Map<String, MutableCell> cells = new HashMap<>();
    private final Map<String, MutableRow> rows = new HashMap<>();

    public Builder(ObjectMapper objectMapper) {
      super();
      this.objectMapper = objectMapper;
    }
    
    private String nextId() {
      return String.valueOf(idGen++);
    }
    private MutableHeader getHeader(String id) {
      Assert.isTrue(headers.containsKey(id), () -> "no header with id: " + id + "!");
      return headers.get(id);
    }
    private MutableCell getCell(String id) {
      Assert.isTrue(cells.containsKey(id), () -> "no cell with id: " + id + "!");
      return cells.get(id);
    }
    private MutableRow getRow(String id) {
      Assert.isTrue(rows.containsKey(id), () -> "no row with id: " + id + "!");
      return rows.get(id);
    }
    private ValueType getValueType(MutableHeader header) {
      return header.getValue();
    }
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    public Builder description(String description) {
      this.description = description;
      return this;
    }
    public Builder hitPolicy(HitPolicy hitPolicy) {
      this.hitPolicy = hitPolicy;
      return this;
    }
    public Builder version(int version) {
      this.version = version;
      return this;
    }
    public Map.Entry<String, Builder> addHeader(Direction direction, String name) {
      MutableHeader header = new MutableHeader(nextId(), direction, headers.size())
          .setName(name)
          .setValue(ValueType.STRING);
      
      headers.put(header.getId(), header);
      this.rows.values().forEach(row -> {
        MutableCell cell = new MutableCell(nextId(), row.getId());
        header.getCells().add(cell);
        cells.put(cell.getId(), cell);
      });
      return new AbstractMap.SimpleImmutableEntry<String, Builder>(header.getId(), this);
    }
    public Builder changeHeaderType(String id, String value) {
      try {
        getHeader(id).setValue(ValueType.valueOf(value));
      } catch(Exception e) {
        getHeader(id).setValue(null);
      }
      
      return this;
    }
    public Builder changeHeaderScript(String id, String value) {
      getHeader(id).setScript(value);
      return this;
    }
    public Builder changeHeaderName(String id, String value) {
      getHeader(id).setName(value);
      return this;
    }
    public Builder changeHeaderDirection(String id, Direction value) {
      MutableHeader header = getHeader(id).setDirection(value);
      ValueType valueType = getValueType(header);

      // Remove expression if cell new direction is out
      if(value == Direction.OUT && valueType != null) {
        header.cells.stream()
        .filter(c -> !StringUtils.isEmpty(c.getValue()))
        .forEach(cell -> {

          try {
            Expression expression = OperationFactory.builder()
                .objectMapper(objectMapper)
                .valueType(valueType)
                .src(cell.getValue()).build();

            if(expression.getConstants().size() == 1) {
              cell.setValue(expression.getConstants().get(0));
            }
          } catch(DecisionAstException e) {
            cell.setValue(cell.getValue());
          }
        });
      }

      return this;
    }

    private String getExpression(ValueType valueType, ColumnExpressionType value, String columnValue) {
      String constant;
      try {
        Expression expression = OperationFactory.builder()
            .objectMapper(objectMapper)
            .valueType(valueType)
            .src(columnValue).build();
        if(expression.getConstants().size() != 1) {
          return null;
        }
        constant = expression.getConstants().get(0);
      } catch(DecisionAstException e) {
        constant = columnValue.trim();
      }
      switch (value) {
      case EQUALS:
        return "= " + constant;
      case IN:
        return "in[\"" + constant + "\"]";
      default:
        return null;
      }
    }

    public Builder setHeaderExpression(String id, ColumnExpressionType value) {
      MutableHeader header = getHeader(id);
      ValueType valueType = getValueType(header);

      if(header.getDirection() == Direction.IN && valueType != null) {
        header.cells.stream()
        .filter(c -> !StringUtils.isEmpty(c.getValue()))
        .forEach(cell -> {
          String operation = getExpression(valueType, value, cell.getValue());
          if(operation != null) {
            cell.setValue(operation);
          }
        });
      }

      return this;
    }
    public Builder changeCell(String id, String value) {
      getCell(id).setValue(value);
      return this;
    }
    public Builder changeCell(String rowId, int columnIndex, String value) {
      MutableHeader column = headers.values().stream().filter(r -> r.order == columnIndex).findFirst().get();
      MutableCell cell = column.getCells().stream().filter(c -> c.getRow().equals(rowId)).findFirst().get();
      cell.setValue(value);
      return this;
    }
    public Builder deleteCell(String id) {
      getCell(id).setValue(null);
      return this;
    }
    public Builder deleteHeader(String id) {
      getHeader(id).getCells().forEach(c -> cells.remove(c.getId()));
      headers.remove(id);
      return this;
    }
    public Map.Entry<String, Builder> addRow() {
      MutableRow row = new MutableRow(nextId(), rows.size());
      rows.put(row.getId(), row);
      
      headers.values().forEach(h -> {
        MutableCell cell = new MutableCell(nextId(), row.getId());
        h.getCells().add(cell);
        cells.put(cell.getId(), cell);
      });
      
      return new AbstractMap.SimpleImmutableEntry<String, Builder>(row.getId(), this);
    }
    public Builder deleteRow(String id) {
      MutableRow row = getRow(id);
      rows.remove(row.getId());
      int order = row.getOrder();
      rows.values().stream()
      .filter(r -> r.getOrder() > order)
      .forEach(r -> r.setOrder(r.getOrder() - 1));
      
      
      headers.values().forEach(h -> {
        Iterator<MutableCell> cell = h.getCells().iterator();
        while(cell.hasNext()) {
          if(id.equals(cell.next().getRow())) {
            cell.remove();
          }
        }
      });
      
      return this;
    }
    public Builder deleteRows() {
      new ArrayList<>(rows.keySet()).forEach(id -> deleteRow(id));
      return this;
    }
    public Builder deleteColumns() {
      new ArrayList<>(headers.keySet()).forEach(id -> deleteHeader(id));
      return this;
    }
    public Builder moveRow(String srcId, String targetId) {
      MutableRow src = getRow(srcId);
      MutableRow target = getRow(targetId);

      int targetOrder = src.getOrder();
      int srcOrder = target.getOrder();
      src.setOrder(srcOrder);
      target.setOrder(targetOrder);
      return this;
    }
    public Builder insertRow(String srcId, String targetId) {
      MutableRow src = getRow(srcId);
      MutableRow target = getRow(targetId);

      // move row from back to front
      if(src.getOrder() > target.getOrder()) {
        int start = target.getOrder();
        int end = src.getOrder();
        
        for(MutableRow row : this.rows.values()) {
          if(row.getOrder() >= start && row.getOrder() < end) {
            row.setOrder(row.getOrder() + 1);
          }
        }
        src.setOrder(start);
      } else {
        // move row from front to back
        int start = src.getOrder();
        int end = target.getOrder();
        
        for(MutableRow row : this.rows.values()) {
          if(row.getOrder() > start && row.getOrder() <= end) {
            row.setOrder(row.getOrder() - 1);
          }
        }
        
        src.setOrder(end);
      }
      return this;
    }
    
    public Builder copyRow(String srcId) {
      MutableRow src = getRow(srcId);
      String targetId = addRow().getKey();
      
      for(MutableHeader header : this.headers.values()) {
        MutableCell from = header.getCells().stream().filter(c -> c.getRow().equals(src.getId())).findFirst().get();
        MutableCell to = header.getCells().stream().filter(c -> c.getRow().equals(targetId)).findFirst().get();
        to.setValue(from.getValue());
      }
      
      return insertRow(targetId, srcId);
    }

    public Builder moveHeader(String srcId, String targetId) {
      MutableHeader src = getHeader(srcId);
      MutableHeader target = getHeader(targetId);

      int targetOrder = src.getOrder();
      int srcOrder = target.getOrder();

      Direction targetDirection = src.getDirection();
      Direction srcDirection = target.getDirection();

      src.setOrder(srcOrder).setDirection(srcDirection);
      target.setOrder(targetOrder).setDirection(targetDirection);
      return this;
    }

    private String resolveScriptValue(
        MutableHeader header, MutableCell cell,
        DynamicValueExpressionExecutor dynamicValueExpressionExecutor) {


      Map<String, Object> context = new HashMap<>();
      for(MutableHeader h : headers.values()) {
        MutableCell value = h.getCells().stream()
            .filter(c -> c.getRow().equals(cell.getRow()))
            .findFirst().get();
        try {
          Object variable = dynamicValueExpressionExecutor.parseVariable(value.getValue(), h.getValue());
          context.put(h.getName(), variable);
        } catch(Exception e) {
        }
      }
      try {
        return dynamicValueExpressionExecutor.execute(header.getScript(), context);
      } catch(Exception e) {
        return null;
      }
    }

    public DecisionAstType build() {
      this.headers.values().stream()
      .filter(h -> !StringUtils.isEmpty(h.getScript()))
      .forEach(h -> h.getCells().forEach(c -> c.setValue(resolveScriptValue(h, c, dynamicValueExpressionExecutor))));

      //Direction direction, String name, String ref, String value, String id, String script, List<String> constraints
      List<Header> headers = this.headers.values().stream().sorted()
          .map(h ->  ImmutableHeader.builder()
          .direction(h.getDirection())
          .name(h.getName())
          .value(h.getValue())
          .id(h.getId())
          .order(h.getOrder())
          .script(h.getScript())
          .build())
        .collect(Collectors.toList());

      
      List<Row> rows = this.rows.values().stream().sorted()
          .map(r -> ImmutableRow.builder()
            .id(r.getId())
            .order(r.getOrder())
            .cells(this.headers.values().stream().sorted()
                .map(h -> {
                  MutableCell c = h.getRowCell(r.getId());
                  return ImmutableCell.builder().id(c.getId()).value(c.getValue()).header(h.getId()).build();
                })
                .collect(Collectors.toList()))
            .build()
          )
          .collect(Collectors.toList());

      HitPolicy hitPolicy = this.hitPolicy == null ? HitPolicy.ALL : this.hitPolicy;
      return ImmutableDecisionAstType.builder()
          .name(name)
          .description(description)
          .rev(version)
          .hitPolicy(hitPolicy)
          .headerTypes(headerTypes)
          .headerExpressions(headerExpressions)
          .headers(headers)
          .rows(rows)
          .build();
    }
  }

  private static class MutableHeader implements Comparable<MutableHeader> {

    private final String id;
    private Direction direction;

    private String script;
    private String name;
    private ValueType value;
    private int order;
    private final List<MutableCell> cells = new ArrayList<>();

    public MutableHeader(String id, Direction direction, int order) {
      super();
      this.id = id;
      this.direction = direction;
      this.order = order;
    }
    public String getScript() {
      return script;
    }
    public void setScript(String script) {
      this.script = script;
    }
    public String getName() {
      return name;
    }
    public MutableHeader setName(String name) {
      this.name = name;
      return this;
    }
    public ValueType getValue() {
      return value;
    }
    public MutableHeader setValue(ValueType value) {
      this.value = value;
      return this;
    }
    public int getOrder() {
      return order;
    }
    public MutableHeader setOrder(int order) {
      this.order = order;
      return this;
    }
    public List<MutableCell> getCells() {
      return cells;
    }
    public MutableCell getRowCell(String rowId) {
      return cells.stream().filter(c -> c.getRow().equals(rowId)).findFirst().get();
    }
    public String getId() {
      return id;
    }
    public Direction getDirection() {
      return direction;
    }
    @Override
    public int compareTo(MutableHeader o) {
      int d0 = direction == Direction.IN ? 0 : 1;
      int d1 = o.getDirection() == Direction.IN ? 0 : 1;

      int direction = Integer.compare(d0, d1);
      if(direction == 0) {
        return Integer.compare(order, o.order);
      }
      return direction;
    }
    public MutableHeader setDirection(Direction direction) {
      this.direction = direction;
      return this;
    }
  }

  private static class MutableCell {
    private final String id;
    private final String row;
    private String value;

    public MutableCell(String id, String row) {
      super();
      this.id = id;
      this.row = row;
    }
    public String getValue() {
      return value;
    }
    public void setValue(String value) {
      this.value = value;
    }
    public String getRow() {
      return row;
    }
    public String getId() {
      return id;
    }
  }
  private static class MutableRow implements Comparable<MutableRow> {
    private final String id;
    private int order;
    public MutableRow(String id, int order) {
      super();
      this.id = id;
      this.order = order;
    }
    public int getOrder() {
      return order;
    }
    public void setOrder(int order) {
      this.order = order;
    }
    public String getId() {
      return id;
    }
    @Override
    public int compareTo(MutableRow o) {
      return Integer.compare(order, o.order);
    }
  }
}

package io.resys.hdes.decisiontable.spi.model;

/*-
 * #%L
 * hdes-decisiontable
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.Expression;
import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.decisiontable.api.DecisionTableCommandException;
import io.resys.hdes.decisiontable.api.DecisionTableCommandType;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.ColumnExpressionType;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.decisiontable.api.DecisionTableModel.HitPolicy;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Row;
import io.resys.hdes.decisiontable.api.DecisionTableService.ModelBuilder;
import io.resys.hdes.decisiontable.api.ImmutableCell;
import io.resys.hdes.decisiontable.api.ImmutableDecisionTableModel;
import io.resys.hdes.decisiontable.api.ImmutableHeader;
import io.resys.hdes.decisiontable.api.ImmutableRow;
import io.resys.hdes.decisiontable.spi.headers.HeaderFactory;
import io.resys.hdes.decisiontable.spi.model.beans.MutableCell;
import io.resys.hdes.decisiontable.spi.model.beans.MutableHeader;
import io.resys.hdes.decisiontable.spi.model.beans.MutableRow;

public class GenericDecisionTableModelBuilder implements ModelBuilder {

  private final DataTypeService expressionBuilder;
  private final HeaderFactory headerFactory;

  private Collection<DataTypeCommand> src;
  private Integer rev;
  
  private int idGen = 0;
  private String name;
  private String description;
  private HitPolicy hitPolicy;
  private int version;
  private final Map<Integer, MutableHeader> headers = new HashMap<>();
  private final Map<Integer, MutableCell> cells = new HashMap<>();
  private final Map<Integer, MutableRow> rows = new HashMap<>();

  public GenericDecisionTableModelBuilder(DataTypeService expressionBuilder, HeaderFactory headerFactory) {
    super();
    this.expressionBuilder = expressionBuilder;
    this.headerFactory = headerFactory;
  }
  
  @Override
  public ModelBuilder src(Collection<DataTypeCommand> src) {
    this.src = src;
    return this;
  }
  @Override
  public ModelBuilder rev(Integer rev) {
    this.rev = rev;
    return this;
  }
  @Override
  public DecisionTableModel build() {
    Collection<DataTypeCommand> src = CollectionUtils.isEmpty(this.src) ? Collections.emptyList() : this.src;
    if(this.rev != null) {
      int limit = this.rev;
      int runningVersion = 0;
      for(DataTypeCommand command : src) {
        if(runningVersion++ > limit) {
          break;
        }
        execute(command);
      }
      version(limit);
    } else {
      src.forEach(this::execute);
      version(src.size());
    }    

    List<Header> headers = this.headers.values().stream().sorted()
        .map(h -> ImmutableHeader.builder()
            .direction(h.getDirection())
            .name(h.getName())
            .ref(h.getRef())
            .value(h.getValue())
            .id(h.getId())
            .constraints(h.getConstraints())
            .build())
        .collect(Collectors.toList());

    List<Row> rows = this.rows.values().stream().sorted()
        .map(r -> ImmutableRow.builder()
            .id(r.getId()).order(r.getOrder())
            .cells(this.headers.values().stream().sorted()
              .map(h -> h.getRowCell(r.getId()))
              .map((MutableCell c)-> ImmutableCell.builder().id(c.getId()).value(c.getValue()).header(c.getId()).build())
              .collect(Collectors.toList())
            ).build()
        )
        .collect(Collectors.toList());
    HitPolicy hitPolicy = this.hitPolicy == null ? HitPolicy.ALL : this.hitPolicy;
    List<DecisionTableModel.Error> errors = new ArrayList<>();
    return ImmutableDecisionTableModel.builder()
        .name(name).description(description).rev(version).hitPolicy(hitPolicy)
        .headerTypes(this.headerFactory.types().stream()
            .collect(Collectors.toMap(h -> h.getRef() != null ? h.getRef() : h.getValue(), h -> h)))
        .headerExpressions(this.headerFactory.typeExpressions())
        .headers(headers)
        .rows(rows)
        .errors(errors)
        .build();
  }

  private GenericDecisionTableModelBuilder execute(DataTypeCommand command) {
    try {
      switch(DecisionTableCommandType.valueOf(command.getType())) {
      case SET_HEADER_SCRIPT:
        return changeHeaderScript(command.getId(), command.getValue());
      case SET_NAME:
        return name(command.getValue());
      case SET_DESCRIPTION:
        return description(command.getValue());
      case SET_HIT_POLICY:
        return hitPolicy(!StringUtils.isEmpty(command.getValue()) ? HitPolicy.valueOf(command.getValue()) : null);
      case MOVE_ROW:
        return moveRow(command.getId(), Integer.parseInt(command.getValue()));
      case MOVE_HEADER:
        return moveHeader(command.getId(), Integer.parseInt(command.getValue()));
      case SET_HEADER_TYPE:
        return changeHeaderType(command.getId(), command.getValue());
      case SET_HEADER_REF:
        return changeHeaderName(command.getId(), command.getValue());
      case SET_HEADER_EXTERNAL_REF:
        return changeHeaderRef(command.getId(), command.getValue());
      case SET_HEADER_DIRECTION:
        return changeHeaderDirection(command.getId(), Direction.valueOf(command.getValue()));
      case SET_HEADER_EXPRESSION:
        return setHeaderExpression(command.getId(), ColumnExpressionType.valueOf(command.getValue()));
      case SET_CELL_VALUE:
        return changeCell(command.getId(), command.getValue());
      case DELETE_CELL:
        return deleteCell(command.getId());
      case DELETE_HEADER:
        return deleteHeader(command.getId());
      case DELETE_HEADER_CONSTRAINT:
        return deleteHeaderConstraint(command.getId(), command.getValue());
      case DELETE_ROW:
        return deleteRow(command.getId());
      case ADD_HEADER_IN:
        addHeader(Direction.IN);
        return this;
      case ADD_HEADER_OUT:
        addHeader(Direction.OUT);
        return this;
      case ADD_HEADER_CONSTRAINT:
        return addHeaderConstraint(command.getId(), command.getValue());
      case ADD_ROW:
        addRow();
        return this;
      case IMPORT_CSV:
        deleteColumns().deleteRows();
        CSVParser parser = CSVParser.parse(command.getValue(), CSVFormat.DEFAULT);
        List<CSVRecord> records = parser.getRecords();
        if(records.isEmpty()) {
          return this;
        }
        Iterator<CSVRecord> iterator = records.iterator();
        CSVRecord csvHeader = iterator.next();
        csvHeader.forEach(c -> {
          Integer id = addHeader(Direction.IN);
          changeHeaderType(id, ValueType.STRING.name());
          changeHeaderName(id, c);
        });

        while(iterator.hasNext()) {
          CSVRecord row = iterator.next();
          int rowId = this.addRow();
          Iterator<String> cellIterator = row.iterator();
          while(cellIterator.hasNext()) {
            changeCell(++rowId, cellIterator.next());
          }
        }
        return this;
      default: return this;
      }
    } catch(HdesException e) {
      throw e;
    } catch(Exception e) {
      throw new DecisionTableCommandException(command, e.getMessage(), e);
    }
  }


  private int nextId() {
    return idGen++;
  }
  private MutableHeader getHeader(int id) {
    Assert.isTrue(headers.containsKey(id), () -> "no header with id: " + id + "!");
    return headers.get(id);
  }
  private MutableCell getCell(int id) {
    Assert.isTrue(cells.containsKey(id), () -> "no cell with id: " + id + "!");
    return cells.get(id);
  }
  private MutableRow getRow(int id) {
    Assert.isTrue(rows.containsKey(id), () -> "no row with id: " + id + "!");
    return rows.get(id);
  }
  private ValueType getValueType(MutableHeader header) {
    try {
      return ValueType.valueOf(header.getValue());
    } catch(Exception e) {
      return null;
    }
  }
  private GenericDecisionTableModelBuilder name(String name) {
    this.name = name;
    return this;
  }
  private GenericDecisionTableModelBuilder description(String description) {
    this.description = description;
    return this;
  }
  private GenericDecisionTableModelBuilder hitPolicy(HitPolicy hitPolicy) {
    this.hitPolicy = hitPolicy;
    return this;
  }
  private GenericDecisionTableModelBuilder version(int version) {
    this.version = version;
    return this;
  }
  private int addHeader(Direction direction) {
    MutableHeader header = new MutableHeader(nextId(), direction, headers.size());
    headers.put(header.getId(), header);
    this.rows.values().forEach(row -> {
      MutableCell cell = new MutableCell(nextId(), row.getId());
      header.getCells().add(cell);
      cells.put(cell.getId(), cell);
    });
    return header.getId();
  }
  private GenericDecisionTableModelBuilder changeHeaderType(Integer id, String value) {
    getHeader(id).setValue(value);
    return this;
  }
  private GenericDecisionTableModelBuilder changeHeaderScript(Integer id, String value) {
    getHeader(id).setScript(value);
    return this;
  }
  private GenericDecisionTableModelBuilder addHeaderConstraint(Integer id, String value) {
    if(!StringUtils.isEmpty(value) && !getHeader(id).getConstraints().contains(value)) {
      getHeader(id).getConstraints().add(value);
    }
    return this;
  }
  private GenericDecisionTableModelBuilder deleteHeaderConstraint(Integer id, String value) {
    getHeader(id).getConstraints().remove(value);
    return this;
  }
  private GenericDecisionTableModelBuilder changeHeaderName(Integer id, String value) {
    getHeader(id).setName(value);
    return this;
  }
  private GenericDecisionTableModelBuilder changeHeaderRef(Integer id, String value) {
    getHeader(id).setRef(value);
    return this;
  }
  private GenericDecisionTableModelBuilder changeHeaderDirection(Integer id, Direction value) {
    MutableHeader header = getHeader(id).setDirection(value);
    ValueType valueType = getValueType(header);

    // Remove expression if cell new direction is out
    if(value == Direction.OUT && valueType != null) {
      header.getCells().stream()
      .filter(c -> !StringUtils.isEmpty(c.getValue()))
      .forEach(cell -> {
          Expression expression = expressionBuilder.expression()
              .valueType(valueType)
              .src(cell.getValue()).build();

          if(expression.getConstants().size() == 1) {
            cell.setValue(expression.getConstants().get(0));
          }
      });
    }

    return this;
  }

  private String getExpression(ValueType valueType, ColumnExpressionType value, String columnValue) {
    String constant;
    Expression expression = expressionBuilder.expression()
      .valueType(valueType)
      .src(columnValue).build();
    if (expression.getConstants().size() != 1) {
      return null;
    }
    constant = expression.getConstants().get(0);

    switch (value) {
      case EQUALS:
        return "= " + constant;
      case IN:
        return "in[\"" + constant + "\"]";
      default:
        return null;
    }
  }

  private GenericDecisionTableModelBuilder setHeaderExpression(Integer id, ColumnExpressionType value) {
    MutableHeader header = getHeader(id);
    ValueType valueType = getValueType(header);

    if(header.getDirection() == Direction.IN && valueType != null) {
      header.getCells().stream()
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
  private GenericDecisionTableModelBuilder changeCell(int id, String value) {
    getCell(id).setValue(value);
    return this;
  }
  private GenericDecisionTableModelBuilder deleteCell(int id) {
    getCell(id).setValue(null);
    return this;
  }
  private GenericDecisionTableModelBuilder deleteHeader(Integer id) {
    getHeader(id).getCells().forEach(c -> cells.remove(c.getId()));
    headers.remove(id);
    return this;
  }
  private int addRow() {
    MutableRow row = new MutableRow(nextId(), rows.size());
    rows.put(row.getId(), row);
    headers.values().forEach(h -> {
      MutableCell cell = new MutableCell(nextId(), row.getId());
      h.getCells().add(cell);
      cells.put(cell.getId(), cell);
    });
    return row.getId();
  }
  private GenericDecisionTableModelBuilder deleteRow(Integer id) {
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
  private GenericDecisionTableModelBuilder deleteRows() {
    new ArrayList<>(rows.keySet()).forEach(id -> deleteRow(id));
    return this;
  }
  private GenericDecisionTableModelBuilder deleteColumns() {
    new ArrayList<>(headers.keySet()).forEach(id -> deleteHeader(id));
    return this;
  }
  private GenericDecisionTableModelBuilder moveRow(int srcId, int targetId) {
    MutableRow src = getRow(srcId);
    MutableRow target = getRow(targetId);

    int targetOrder = src.getOrder();
    int srcOrder = target.getOrder();
    src.setOrder(srcOrder);
    target.setOrder(targetOrder);
    return this;
  }
  private GenericDecisionTableModelBuilder moveHeader(int srcId, int targetId) {
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

}

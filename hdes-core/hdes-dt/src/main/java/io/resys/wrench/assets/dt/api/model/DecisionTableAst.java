package io.resys.wrench.assets.dt.api.model;

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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.wrench.assets.datatype.api.AstType;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.Direction;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.ValueType;
import io.resys.wrench.assets.dt.api.model.DecisionTable.HitPolicy;

@Value.Immutable
public interface DecisionTableAst extends AstType, Serializable {
  
  HitPolicy getHitPolicy();
  List<String> getHeaderTypes();
  Map<ValueType, List<String>> getHeaderExpressions();
  List<Header> getHeaders();
  List<Row> getRows();

  @Value.Immutable
  interface Header extends Serializable {
    String getId();
    Integer getOrder();
    String getName();
    ValueType getValue();
    Direction getDirection();
    
    @Nullable
    String getScript();
  }

  @Value.Immutable
  interface Row extends Serializable {
    String getId();
    int getOrder();
    List<Cell> getCells();
  }

  @Value.Immutable
  interface Cell extends Serializable {
    String getId();
    String getHeader();
    @Nullable    
    String getValue();
  }

  @Value.Immutable  
  interface Command extends Serializable {
    @Nullable
    String getId();
    @Nullable
    String getValue();
    CommandType getType();
  }

  enum CommandType {
    SET_NAME,
    SET_DESCRIPTION,
    IMPORT_CSV,    
    IMPORT_ORDERED_CSV,

    MOVE_ROW,
    MOVE_HEADER,
    INSERT_ROW,
    COPY_ROW,
    
    SET_HEADER_TYPE,
    SET_HEADER_REF,
    
    SET_HEADER_SCRIPT,
    SET_HEADER_DIRECTION,
    SET_HEADER_EXPRESSION,
    SET_HIT_POLICY,
    SET_CELL_VALUE,

    DELETE_CELL,
    DELETE_HEADER,
    DELETE_ROW,

    ADD_LOG,
    ADD_HEADER_IN,
    ADD_HEADER_OUT,
    ADD_ROW,
  }

  enum ColumnExpressionType { IN, EQUALS }
}

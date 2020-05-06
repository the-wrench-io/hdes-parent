package io.resys.hdes.decisiontable.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.reactivex.annotations.Nullable;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;

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

@Value.Immutable
@JsonSerialize(as = ImmutableDecisionTableModel.class)
@JsonDeserialize(as = ImmutableDecisionTableModel.class)
public interface DecisionTableModel extends Serializable {
  String getName();
  int getRev();
  HitPolicy getHitPolicy();
  @Nullable
  String getDescription();
  
  Map<String, HeaderType> getHeaderTypes();
  Map<ValueType, Collection<String>> getHeaderExpressions();
  List<Header> getHeaders();
  Collection<Row> getRows();
  Collection<Error> getErrors();

  @Value.Immutable
  @JsonSerialize(as = ImmutableError.class)
  @JsonDeserialize(as = ImmutableError.class)
  interface Error extends Serializable {
    String getMessage();
    String getTarget();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableHeader.class)
  @JsonDeserialize(as = ImmutableHeader.class)
  interface Header extends Serializable {
    int getId();
    String getName();
    List<String> getConstraints();
    Direction getDirection();
    
    @Nullable
    String getRef();
    @Nullable
    String getValue();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableHeaderType.class)
  @JsonDeserialize(as = ImmutableHeaderType.class)
  interface HeaderType extends Serializable {
    @Nullable
    String getName();
    @Nullable
    String getRef();
    @Nullable
    String getValue();
    @Nullable
    Collection<String> getValues();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableRow.class)
  @JsonDeserialize(as = ImmutableRow.class)
  interface Row extends Serializable {
    int getId();
    int getOrder();
    List<Cell> getCells();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableCell.class)
  @JsonDeserialize(as = ImmutableCell.class)
  interface Cell extends Serializable {
    int getId();
    int getHeader();
    @Nullable
    String getValue();
  }

  enum ColumnExpressionType {
    IN, EQUALS
  }
  
  enum HitPolicy {
    FIRST, ALL
  }
}

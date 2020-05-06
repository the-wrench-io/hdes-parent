package io.resys.hdes.decisiontable.spi.headers;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.ImmutableHeaderType;

public class GenericHeaderFactory implements HeaderFactory {

  private final Map<DataType.ValueType, Collection<String>> typeExpressions;
  private final Collection<DecisionTableModel.HeaderType> types;

  public GenericHeaderFactory(Map<DataType.ValueType, Collection<String>> typeExpressions,
                              Collection<DecisionTableModel.HeaderType> types) {
    this.typeExpressions = typeExpressions;
    this.types = types;
  }

  @Override
  public Map<DataType.ValueType, Collection<String>> typeExpressions() {
    return typeExpressions;
  }

  @Override
  public Collection<DecisionTableModel.HeaderType> types() {
    return types;
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {
    private final static String EMPTY_REF = "";
    private final Map<DataType.ValueType, Collection<String>> typeExpressions = new HashMap<>();
    private final Map<String, DecisionTableModel.HeaderType> types = new HashMap<>();

    private Config() {
      Arrays.asList(
        DataType.ValueType.STRING,
        DataType.ValueType.BOOLEAN,
        DataType.ValueType.INTEGER,
        DataType.ValueType.LONG,
        DataType.ValueType.DECIMAL,
        DataType.ValueType.DATE,
        DataType.ValueType.DATE_TIME).stream()
        .map(v -> ImmutableHeaderType.builder().value(v.name()).build())
        .forEach(this::add);

      this.add(DataType.ValueType.INTEGER, DecisionTableModel.ColumnExpressionType.EQUALS).
        add(DataType.ValueType.DECIMAL, DecisionTableModel.ColumnExpressionType.EQUALS).
        add(DataType.ValueType.STRING, DecisionTableModel.ColumnExpressionType.IN);
    }


    public Config add(DecisionTableModel.HeaderType type) {
      this.types.put(type.getName() + "/" + Optional.ofNullable(type.getRef()).orElseGet(() -> EMPTY_REF), type);
      return this;
    }

    public Config add(DataType.ValueType valueType, DecisionTableModel.ColumnExpressionType ... type) {
      typeExpressions.put(
        valueType,
        Collections.unmodifiableList(
            Arrays.asList(type).stream()
            .map(v -> v.name()).collect(Collectors.toList())));
      return this;
    }

    public GenericHeaderFactory build() {
      return new GenericHeaderFactory(
        Collections.unmodifiableMap(typeExpressions),
        Collections.unmodifiableCollection(types.values()));
    }
  }
}

package io.resys.hdes.datatype.spi.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/*-
 * #%L
 * wrench-assets-datatypes
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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.DataTypeConstraint;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.ModelBuilder;
import io.resys.hdes.datatype.api.ImmutableDataType;
import io.resys.hdes.datatype.api.exceptions.DataTypeModelException;
import io.resys.hdes.datatype.spi.Assert;

public class DataTypeBuilder implements DataTypeService.ModelBuilder {
  private final boolean child;
  private Boolean required;
  private Boolean array;
  private String name;
  private ValueType valueType;
  private Direction direction;
  private Class<?> beanType;
  private String description;
  private List<String> values = new ArrayList<>();
  private List<DataType> properties = new ArrayList<>();
  private List<DataTypeConstraint> constraints = new ArrayList<>();
  private String ref;
  private DataType dataType;


  public DataTypeBuilder() {
    super();
    this.child = false;
  }
  
  private DataTypeBuilder(boolean child) {
    super();
    this.child = child;
  }
  
  public static DataTypeService.ModelBuilder create() {
    return new DataTypeBuilder();
  }

  @Override
  public ModelBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  @Override
  public ModelBuilder array(boolean array) {
    this.array = array;
    return this;
  }

  @Override
  public ModelBuilder name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public ModelBuilder valueType(ValueType valueType) {
    this.valueType = valueType;
    return this;
  }

  @Override
  public ModelBuilder direction(Direction direction) {
    this.direction = direction;
    return this;
  }

  @Override
  public ModelBuilder beanType(Class<?> beanType) {
    this.beanType = beanType;
    return this;
  }

  @Override
  public ModelBuilder description(String description) {
    this.description = description;
    return this;
  }

  @Override
  public ModelBuilder values(Collection<String> values) {
    this.values.addAll(values);
    return this;
  }

  @Override
  public DataTypeService.ConstraintBuilder constraint() {
    return new GenericDataTypeConstraintBuilder() {
      @Override
      public DataTypeConstraint build() {
        DataTypeConstraint result = super.build();
        constraints.add(result);
        return result;
      }
    };
  }

  @Override
  public ModelBuilder ref(String ref, DataType dataType) {
    Assert.isTrue(ref != null, () -> "ref can't be null!");
    Assert.isTrue(dataType != null, () -> "dataType can't be null for ref: " + ref + "!");
    this.ref = ref;
    this.dataType = dataType;
    return this;
  }

  @Override
  public ModelBuilder property() {
    return new DataTypeBuilder(true) {
      @Override
      public DataType build() {
        DataType property = super.build();
        properties.add(property);
        return property;
      }
    };
  }

  @Override
  public ModelBuilder property(Consumer<ModelBuilder> consumer) {
    consumer.accept(this.property());
    return this;
  }
  
  @Override
  public DataType build() {
    try {
      if(!child) {
        Assert.notNull(name, () -> "name can't be null!");
      }
      Direction direction = valueType == ValueType.METHOD_INVOCATION ? Direction.OUT: this.direction;
      
      if (dataType != null) {
        valueType = dataType.getValueType();
        constraints.addAll(dataType.getConstraints());
        properties.addAll(dataType.getProperties());
        return ImmutableDataType.builder()
            .name(name)
            .ref(ref)
            .description(description)
            .valueType(valueType)
            .beanType(beanType)
            .isArray(Boolean.TRUE.equals(array))
            .isRequired(Boolean.TRUE.equals(required))
            .values(values)
            .constraints(constraints)
            .properties(properties)
            .build();
      }
      Assert.notNull(valueType, () -> "valueType can't be null!");
      return ImmutableDataType.builder()
          .name(name)
          .ref(ref)
          .isArray(Boolean.TRUE.equals(array))
          .description(description)
          .direction(direction)
          .valueType(valueType)
          .beanType(beanType)
          .isRequired(Boolean.TRUE.equals(required))
          .values(values)
          .constraints(constraints)
          .properties(properties)
          .build();
    } catch (Exception e) {
      throw DataTypeModelException.builder().original(e).name(name).type(valueType).build();
    }
  }

  private static class GenericDataTypeConstraintBuilder implements DataTypeService.ConstraintBuilder {
    private List<String> values;

    @Override
    public GenericDataTypeConstraintBuilder values(List<String> values) {
      Assert.isTrue(values != null && !values.isEmpty(), () -> "values can't be empty!");
      this.values = new ArrayList<>(values);
      return this;
    }

    @Override
    public DataTypeConstraint build() {
      Assert.isTrue(values != null && !values.isEmpty(), () -> "values can't be empty!");
      return new ValuesDataTypeConstraint(values);
    }
  }

  private static class ValuesDataTypeConstraint implements DataType.DataTypeConstraint {
    private final List<String> values;

    private ValuesDataTypeConstraint(List<String> values) {
      super();
      this.values = values;
    }

    @Override
    public DataType.ConstraintType getType() {
      return DataType.ConstraintType.VALUES;
    }

    @SuppressWarnings("unused")
    public List<String> getValues() {
      return values;
    }
  }
}
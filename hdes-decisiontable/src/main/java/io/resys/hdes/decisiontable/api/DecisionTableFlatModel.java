package io.resys.hdes.decisiontable.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*-
 * #%L
 * wrench-component-dt
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.decisiontable.api.DecisionTableModel.HitPolicy;


@JsonIgnoreProperties(allowGetters = true, allowSetters = false)
public class DecisionTableFlatModel implements Serializable {
  private static final long serialVersionUID = -4550522375835493816L;

  private HitPolicy hitPolicy;
  private String name;
  private String description;

  private List<Type> types;
  private List<Entry> entries;

  public HitPolicy getHitPolicy() {
    return hitPolicy;
  }
  public DecisionTableFlatModel setHitPolicy(HitPolicy hitPolicy) {
    this.hitPolicy = hitPolicy;
    return this;
  }
  public String getName() {
    return name;
  }
  public DecisionTableFlatModel setName(String name) {
    this.name = name;
    return this;
  }
  public String getDescription() {
    return description;
  }
  public DecisionTableFlatModel setDescription(String description) {
    this.description = description;
    return this;
  }
  public List<Type> getTypes() {
    if(types == null) {
      types = new ArrayList<>();
    }
    return types;
  }
  public void setTypes(List<Type> types) {
    this.types = types;
  }
  public List<Entry> getEntries() {
    if(entries == null) {
      entries = new ArrayList<>();
    }
    return entries;
  }
  public void setEntries(List<Entry> entries) {
    this.entries = entries;
  }

  public static class Entry implements Serializable {
    private static final long serialVersionUID = -2260677725042621127L;

    @JsonIgnore
    private Integer id;
    private List<Value> values;

    public Integer getId() {
      return id;
    }
    public Entry setId(Integer id) {
      this.id = id;
      return this;
    }
    public List<Value> getValues() {
      if(values == null) {
        values = new ArrayList<>();
      }
      return values;
    }
    public void setValues(List<Value> values) {
      this.values = values;
    }
  }

  public static class Value implements Serializable {
    private static final long serialVersionUID = -4579676372923801659L;

    private Integer id;
    private String value;

    public Integer getId() {
      return id;
    }
    public Value setId(Integer id) {
      this.id = id;
      return this;
    }
    public String getValue() {
      return value;
    }
    public Value setValue(String value) {
      this.value = value;
      return this;
    }

  }

  public static class Type implements Serializable {
    private static final long serialVersionUID = 4660188528469460957L;

    @JsonIgnore
    private Integer id;
    private String name;
    private String type;
    private DataType.Direction direction;

    public DataType.Direction getDirection() {
      return direction;
    }
    public Type setDirection(DataType.Direction direction) {
      this.direction = direction;
      return this;
    }
    public String getName() {
      return name;
    }
    public Type setName(String name) {
      this.name = name;
      return this;
    }
    public String getType() {
      return type;
    }
    public Type setType(String type) {
      this.type = type;
      return this;
    }
    public Integer getId() {
      return id;
    }
    public Type setId(int id) {
      this.id = id;
      return this;
    }
  }
}

package io.resys.wrench.assets.bundle.spi.flow.executors;

/*-
 * #%L
 * wrench-component-assets-activiti
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

public class FlowVariable implements Serializable {

  private static final long serialVersionUID = 2449657861529629506L;

  private Class<?> type;
  private Set<FlowVariableField> fields = new HashSet<>();
  private Serializable entity;

  public FlowVariable() {
  }

  public FlowVariable(Serializable entity) {
    this.entity = entity;
    this.type = entity.getClass();
  }

  public FlowVariable(Class<?> clazz) {
    this.type = clazz;
  }

  public Class<?> getType() {
    return type;
  }

  public FlowVariable setType(Class<?> type) {
    this.type = type;
    return this;
  }

  public Set<FlowVariableField> getFields() {
    return fields;
  }

  public FlowVariable setFields(Set<FlowVariableField> fields) {
    this.fields = fields;
    return this;
  }

  public FlowVariable addField(String name, Comparable<?> value) {
    fields.add(new FlowVariableField(name, value));
    return this;
  }

  public Object getValue() {
    Assert.isTrue(fields.size() == 1, "expecting 1 value but was: " + fields.size() + "!");
    return fields.iterator().next().getValue();
  }


  public Serializable getEntity() {
    return entity;
  }

  public FlowVariable setEntity(Serializable entity) {
    this.entity = entity;
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + ((fields == null) ? 0 : fields.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FlowVariable other = (FlowVariable) obj;
    if (entity == null) {
      if (other.entity != null)
        return false;
    } else if (!entity.equals(other.entity))
      return false;
    if (fields == null) {
      if (other.fields != null)
        return false;
    } else if (!fields.equals(other.fields))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
}

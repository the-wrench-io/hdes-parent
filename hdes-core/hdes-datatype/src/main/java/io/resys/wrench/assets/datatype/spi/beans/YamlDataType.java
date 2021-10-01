package io.resys.wrench.assets.datatype.spi.beans;

import java.util.Collections;

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

import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.ast.AstType.ValueType;

public class YamlDataType {

  private ValueType type;
  private Integer min;
  private Integer max;
  private String pattern;
  private List<String> values;
  private Map<String, YamlDataType> properties;

  public ValueType getType() {
    return type;
  }
  public void setType(ValueType type) {
    this.type = type;
  }
  public Integer getMin() {
    return min;
  }
  public void setMin(Integer min) {
    this.min = min;
  }
  public Integer getMax() {
    return max;
  }
  public void setMax(Integer max) {
    this.max = max;
  }
  public String getPattern() {
    return pattern;
  }
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }
  public List<String> getValues() {
    return values;
  }
  public void setValues(List<String> values) {
    this.values = values;
  }
  public Map<String, YamlDataType> getProperties() {
    if(properties == null) {
      return Collections.emptyMap();
    }
    return properties;
  }
  public void setProperties(Map<String, YamlDataType> properties) {
    this.properties = properties;
  }

}

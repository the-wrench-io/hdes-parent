package io.resys.wrench.assets.flow.spi.support;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import io.resys.wrench.assets.flow.api.model.FlowAst.FlowAutocomplete;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandRange;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAutocomplete;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowCommandRange;

public class FlowNodesFactory {

  public static AcBuilder ac() {
    return new AcBuilder();
  }
  
  public static RangeBuilder range() {
    return new RangeBuilder();
  }
  
  public static class RangeBuilder {
    
    public ImmutableFlowCommandRange build(int start, int end, Boolean insert) {
      return ImmutableFlowCommandRange.builder().start(start).end(end).insert(insert).build();
    }

    public ImmutableFlowCommandRange build(int start, int end, Boolean insert, Integer column) {
      return ImmutableFlowCommandRange.builder().start(start).end(end).insert(insert).column(column).build();
    }

    public ImmutableFlowCommandRange build(int start, int end) {
      return ImmutableFlowCommandRange.builder().start(start).end(end).build();
    }

    public ImmutableFlowCommandRange build(int start) {
      return ImmutableFlowCommandRange.builder().start(start).end(start).build();
    }
  }

  public static class AcBuilder {
    private static final String FIELD = ":";
    private String id;
    private final Collection<FlowCommandRange> range = new ArrayList<>();
    private final Collection<String> value = new ArrayList<>();

    public AcBuilder id(String id) {
      this.id = id;
      return this;
    }

    public AcBuilder addRange(int start, int end) {
      range.add(FlowNodesFactory.range().build(start, end));
      return this;
    }
    public AcBuilder addRange(FlowCommandRange range) {
      this.range.add(range);
      return this;
    }
    public AcBuilder addRange(Collection<FlowCommandRange> range) {
      this.range.addAll(range);
      return this;
    }
    public AcBuilder addField(int indent, String fieldName) {
      this.value.add(getIndent(indent) + fieldName + FIELD);
      return this;
    }

    public AcBuilder addField(int indent, String fieldName, Serializable value) {
      this.value.add(getIndent(indent) + fieldName + FIELD + " " + value);
      return this;
    }

    public AcBuilder addField(String fieldName) {
      this.value.add(fieldName + FIELD);
      return this;
    }

    public AcBuilder addField(String fieldName, Serializable value) {
      this.value.add(fieldName + FIELD + " " + value);
      return this;
    }

    public AcBuilder addValue(String ... value) {
      this.value.addAll(Arrays.asList(value));
      return this;
    }
    public AcBuilder addValue(Collection<String> value) {
      this.value.addAll(value);
      return this;
    }
    private String getIndent(int indent) {
      StringBuilder result = new StringBuilder();
      for(int index = 0; index < indent; index++) {
        result.append(" ");
      }
      return result.toString();
    }
    public FlowAutocomplete build() {
      return ImmutableFlowAutocomplete.builder()
          .id(id)
          .range(range)
          .value(value)
          .build();
    }

  }
}

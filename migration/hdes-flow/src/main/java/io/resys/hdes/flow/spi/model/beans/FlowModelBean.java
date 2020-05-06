package io.resys.hdes.flow.spi.model.beans;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.resys.hdes.flow.api.FlowModel;

@JsonIgnoreProperties({"parent"})
public class FlowModelBean implements FlowModel {
  private static final long serialVersionUID = 5409590378906097144L;
  private final String keyword;
  private final String value;
  private final int indent;
  private final FlowModelSourceBean source;
  private final FlowModelBean parent;
  private final Map<String, FlowModelBean> children = new HashMap<>();
  private Integer end;

  public FlowModelBean(FlowModelSourceBean source, int indent, String keyword, String value, FlowModelBean parent) {
    super();
    this.source = source;
    this.keyword = keyword;
    this.value = value;
    this.parent = parent;
    this.indent = indent;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
  @Override
  public String getValue() {
    return value;
  }
  @Override
  public FlowModelBean getParent() {
    return parent;
  }
  @Override
  public Map<String, FlowModel> getChildren() {
    return Collections.unmodifiableMap(children);
  }
  public int getIndent() {
    return indent;
  }
  @Override
  public FlowModelSourceBean getSource() {
    return source;
  }
  @Override
  public FlowModelBean get(String keyword) {
    return children.get(keyword);
  }
  public boolean contains(String keyword) {
    return children.get(keyword) != null;
  }
  public FlowModelBean addChild(FlowModelSourceBean source, int indent, String keyword, String value) {
    FlowModelBean result = new FlowModelBean(source, indent, keyword, value, this);
    children.put(keyword, result);
    return result;
  }
  public FlowModelBean addChild(FlowModelBean result) {
    children.put(result.getKeyword(), result);
    return result;
  }
  @Override
  public int getEnd() {
    if(end == null) {
      end = getStart();
      for(FlowModelBean node : children.values()) {
        if(end < node.getEnd()) {
          end = node.getEnd();
        }
      }
    }
    return end;
  }
  public FlowModelBean setEnd(int end) {
    this.end = end;
    return this;
  }

  @Override
  public boolean hasNonNull(String name) {
    return this.get(name) != null;
  }

  @Override
  public int getStart() {
    return source == null ? 0 : source.getLine();
  }

  @Override
  public int compareTo(FlowModel o) {
    return Integer.compare(this.getStart(), o.getStart());
  }
}

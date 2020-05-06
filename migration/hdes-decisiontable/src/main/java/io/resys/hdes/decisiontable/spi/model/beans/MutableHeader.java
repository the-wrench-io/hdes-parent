package io.resys.hdes.decisiontable.spi.model.beans;

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
import java.util.List;

import io.resys.hdes.datatype.api.DataType.Direction;

public class MutableHeader implements Comparable<MutableHeader> {

  private final int id;
  private Direction direction;

  private String script;
  private String name;
  private String value;
  private String ref;
  private int order;
  private final List<MutableCell> cells = new ArrayList<>();
  private final List<String> constraints = new ArrayList<>();

  public MutableHeader(int id, Direction direction, int order) {
    super();
    this.id = id;
    this.direction = direction;
    this.order = order;
  }
  public String getScript() {
    return script;
  }
  public void setScript(String script) {
    this.script = script;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public int getOrder() {
    return order;
  }
  public MutableHeader setOrder(int order) {
    this.order = order;
    return this;
  }
  public List<MutableCell> getCells() {
    return cells;
  }
  public MutableCell getRowCell(int rowId) {
    return cells.stream().filter(c -> c.getRow() == rowId).findFirst().get();
  }
  public int getId() {
    return id;
  }
  public Direction getDirection() {
    return direction;
  }
  @Override
  public int compareTo(MutableHeader o) {
    int d0 = direction == Direction.IN ? 0 : 1;
    int d1 = o.getDirection() == Direction.IN ? 0 : 1;

    int direction = Integer.compare(d0, d1);
    if(direction == 0) {
      return Integer.compare(order, o.order);
    }
    return direction;
  }
  public List<String> getConstraints() {
    return constraints;
  }
  public MutableHeader setDirection(Direction direction) {
    this.direction = direction;
    return this;
  }
  public String getRef() {
    return ref;
  }
  public void setRef(String ref) {
    this.ref = ref;
  }
}

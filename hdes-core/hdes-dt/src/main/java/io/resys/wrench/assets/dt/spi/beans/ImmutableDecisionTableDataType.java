package io.resys.wrench.assets.dt.spi.beans;

/*-
 * #%L
 * hdes-dt
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.client.api.model.DataType;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableDataType;

public class ImmutableDecisionTableDataType implements DecisionTableDataType {

  private static final long serialVersionUID = 5025265615138518092L;

  private final int order;
  private final String script;
  private final DataType value;

  public ImmutableDecisionTableDataType(int order, String script, DataType value) {
    super();
    this.order = order;
    this.script = script;
    this.value = value;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public String getScript() {
    return script;
  }

  @Override
  public DataType getValue() {
    return value;
  }

  @Override
  public int compareTo(DecisionTableDataType o) {
    return Integer.compare(order, o.getOrder());
  }
}

package io.resys.wrench.assets.flow.spi.model;

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

import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeInputType;

public class ImmutableNodeInputType implements NodeInputType {

  private static final long serialVersionUID = -7990889282500671347L;
  private final String name;
  private final String ref;
  private final String value;

  public ImmutableNodeInputType(String name, String ref, String value) {
    super();
    this.name = name;
    this.ref = ref;
    this.value = value;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getRef() {
    return ref;
  }

  @Override
  public String getValue() {
    return value;
  }
}

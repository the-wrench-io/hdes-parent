package io.resys.wrench.assets.dt.spi.beans;

/*-
 * #%L
 * wrench-assets-dt
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

import io.resys.hdes.client.api.ast.AstDecision.HitPolicy;
import io.resys.hdes.client.api.execution.DecisionProgram;

public class ImmutableDecisionTable implements DecisionProgram {

  private static final long serialVersionUID = -8964213143541619331L;

  private final String id;
  private final String src;
  private final String rev;
  private final String description;
  private final HitPolicy hitPolicy;
  private final List<DecisionTableDataType> types;
  private final Row node;

  public ImmutableDecisionTable(
      String id, String rev, String src, String description, HitPolicy hitPolicy,
      List<DecisionTableDataType> types,
      Row node) {
    super();
    this.id = id;
    this.rev = rev;
    this.description = description;
    this.types = types;
    this.node = node;
    this.src = src;
    this.hitPolicy = hitPolicy;
  }

  @Override
  public String getId() {
    return id;
  }
  @Override
  public List<DecisionTableDataType> getTypes() {
    return types;
  }
  @Override
  public Row getRows() {
    return node;
  }
  @Override
  public String getSrc() {
    return src;
  }
  @Override
  public String getDescription() {
    return description;
  }
  @Override
  public HitPolicy getHitPolicy() {
    return hitPolicy;
  }
  @Override
  public String getRev() {
    return rev;
  }
}

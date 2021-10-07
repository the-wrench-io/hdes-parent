package io.resys.hdes.client.api.ast;

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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.TypeDef.ValueType;

@Value.Immutable
public interface AstDecision extends AstBody, Serializable {
  
  HitPolicy getHitPolicy();
  List<String> getHeaderTypes();
  Map<ValueType, List<String>> getHeaderExpressions();
  List<Row> getRows();


  @Value.Immutable
  interface Row extends Serializable {
    String getId();
    int getOrder();
    List<Cell> getCells();
  }

  @Value.Immutable
  interface Cell extends Serializable {
    String getId();
    String getHeader();
    @Nullable    
    String getValue();
  }

  enum HitPolicy { FIRST, ALL }
  enum ColumnExpressionType { IN, EQUALS }
}

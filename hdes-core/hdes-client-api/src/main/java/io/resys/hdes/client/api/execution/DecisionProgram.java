package io.resys.hdes.client.api.execution;

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

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.AstDecision.HitPolicy;
import io.resys.hdes.client.api.ast.TypeDef;

@Value.Immutable
public interface DecisionProgram extends Program {
  String getId();
  HitPolicy getHitPolicy();
  List<Row> getRows();

  @Value.Immutable
  interface Row extends Serializable {
    int getOrder();
    
    List<RowAccepts> getAccepts();
    List<RowReturns> getReturns();
  }

  @Value.Immutable
  interface RowAccepts extends Serializable {
    TypeDef getKey();
    ExpressionProgram getExpression();
  }
  
  @Value.Immutable
  interface RowReturns extends Serializable {
    TypeDef getKey();
    Serializable getValue();
  }
}

package io.resys.hdes.decisiontable.spi.headers;

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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.decisiontable.api.DecisionTableModel;

import java.util.Collection;
import java.util.Map;

public interface HeaderFactory {
  /**
   * @return mass operation for all the "rules", example: apply to all rules in("{ruleValue}")
   */
  Map<DataType.ValueType, Collection  <String>> typeExpressions();

  /**
   * @return supported types on DT
   */
  Collection<DecisionTableModel.HeaderType> types();
}

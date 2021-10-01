package io.resys.wrench.assets.datatype.spi.builders;

/*-
 * #%L
 * wrench-assets-datatype
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

import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.client.api.model.DataType.DataTypeConstraint;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataTypeConstraintBuilder;
import io.resys.wrench.assets.datatype.spi.beans.ValuesDataTypeConstraint;
import io.resys.wrench.assets.datatype.spi.util.Assert;

public class GenericDataTypeConstraintBuilder implements DataTypeConstraintBuilder {

  private List<String> values;

  @Override
  public DataTypeConstraintBuilder values(List<String> values) {
    Assert.isTrue(values != null && !values.isEmpty(), () -> "values can't be empty!");
    this.values = new ArrayList<>(values);
    return this;
  }

  @Override
  public DataTypeConstraint build() {
    Assert.isTrue(values != null && !values.isEmpty(), () -> "values can't be empty!");
    return new ValuesDataTypeConstraint(values);
  }
}

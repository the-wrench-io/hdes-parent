package io.resys.wrench.assets.bundle.spi.flow;

/*-
 * #%L
 * wrench-assets-bundle
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstInputType;
import io.resys.hdes.client.api.ast.ImmutableFlowAstInputType;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.spi.builders.DataTypeRefBuilder;
import io.resys.wrench.assets.bundle.spi.builders.DataTypeRefBuilder.DataTypeRef;

public class FlowDataTypeSupplier implements Supplier<Collection<FlowAstInputType>> {

  private final Collection<ValueType> supportedTypes = Arrays.asList(ValueType.ARRAY, 
      ValueType.TIME, ValueType.STRING, ValueType.BOOLEAN, ValueType.INTEGER, ValueType.LONG, ValueType.DECIMAL, 
      ValueType.DATE, ValueType.DATE_TIME);
  private final ServiceStore serviceStore;

  public FlowDataTypeSupplier(ServiceStore serviceStore) {
    super();
    this.serviceStore = serviceStore;
  }

  @Override
  public List<FlowAstInputType> get() {
    List<FlowAstInputType> result = new ArrayList<>();

    // Normal types
    for(ValueType valueType : supportedTypes) {
      //(String name, String ref, String value)
      
      result.add(
          ImmutableFlowAstInputType.builder()
          .name(valueType.name())
          .value(valueType.name())
          .build()
          //new ImmutableNodeInputType(valueType.name(), null, valueType.name())
          );
    }

    // Reference type
    for(AssetService service : serviceStore.list()) {
      List<AstDataType> types = service.getDataModel().getParams();
      for(AstDataType type : types) {
        if(supportedTypes.contains(type.getValueType())) {
          DataTypeRef ref = DataTypeRefBuilder
              .of(service.getType())
              .service(service.getName())
              .name(type.getName())
              .build();
          result.add(
              ImmutableFlowAstInputType.builder()
              .name(ref.getValue())
              .ref(ref.getValue())
              .value(type.getValueType().name())
              .build());
        }
      }
    }
    return result;
  }
}

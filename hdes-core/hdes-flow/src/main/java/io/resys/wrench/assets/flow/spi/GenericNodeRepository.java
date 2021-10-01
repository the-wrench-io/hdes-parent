package io.resys.wrench.assets.flow.spi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.FlowAstType.FlowCommandMessage;
import io.resys.hdes.client.api.ast.FlowAstType.NodeInputType;
import io.resys.wrench.assets.flow.api.FlowAstFactory;
import io.resys.wrench.assets.flow.spi.builders.GenericNodeBuilder;
import io.resys.wrench.assets.flow.spi.model.ImmutableNodeInputType;

public class GenericNodeRepository implements FlowAstFactory {

  private final ObjectMapper yamlMapper;
  private final Supplier<Collection<NodeInputType>> inputTypes;

  public GenericNodeRepository(ObjectMapper yamlMapper, Supplier<Collection<NodeInputType>> inputTypes) {
    super();
    this.yamlMapper = yamlMapper;
    this.inputTypes = inputTypes != null ? inputTypes : () -> Collections.unmodifiableList(
        Arrays.asList(ValueType.STRING,  ValueType.BOOLEAN, ValueType.INTEGER, ValueType.LONG, ValueType.DECIMAL, ValueType.DATE, ValueType.DATE_TIME).stream()
        .map(v -> new ImmutableNodeInputType(v.name(), null, v.name()))
        .collect(Collectors.toList()));
  }

  @Override
  public NodeBuilder create(Consumer<FlowCommandMessage> messageConsumer) {
    return new GenericNodeBuilder(yamlMapper, inputTypes.get(), messageConsumer);
  }
}

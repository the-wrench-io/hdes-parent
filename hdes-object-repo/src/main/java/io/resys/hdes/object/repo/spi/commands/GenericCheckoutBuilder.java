package io.resys.hdes.object.repo.spi.commands;

/*-
 * #%L
 * hdes-object-repo
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

import java.util.Arrays;
import java.util.List;

import io.resys.hdes.object.repo.api.ImmutableHead;
import io.resys.hdes.object.repo.api.ObjectRepository.CheckoutBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Snapshot;

public abstract class GenericCheckoutBuilder implements CheckoutBuilder {

  private final Objects objects;
  private String name;

  public GenericCheckoutBuilder(Objects objects) {
    super();
    this.objects = objects;
  }
  
  @Override
  public CheckoutBuilder from(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Objects build() {
    Snapshot snapshot = new GenericSnapshotBuilder(objects).from(name).build();
    Head head = ImmutableHead.builder().value(name).snapshot(snapshot).build();
    return save(Arrays.asList(head));
  }
  
  protected abstract Objects save(List<Object> newObjects);
}

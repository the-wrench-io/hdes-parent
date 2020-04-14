package io.resys.hdes.object.repo.spi.mapper;

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

import java.util.function.Function;

import io.resys.hdes.object.repo.api.ObjectRepository.Objects;

public class GenericObjectRepositoryMapper<T> implements ObjectRepositoryMapper<T> {
   
  private final static IdSupplier ID_SUPPLIER = new Sha1IdSupplier();
  private final Function<Objects, Writer<T>> writer;
  private final Function<Objects, Delete<T>> delete;
  private final Serializer serializer;
  private final Deserializer deserializer;

  public GenericObjectRepositoryMapper(
      Serializer serializer,
      Deserializer deserializer,
      Function<Objects, Writer<T>> writer,
      Function<Objects, Delete<T>> delete) {
    super();
    this.writer = writer;
    this.delete = delete;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  @Override
  public Serializer serializer() {
    return serializer;
  }

  @Override
  public Deserializer deserializer() {
    return deserializer;
  }

  @Override
  public Writer<T> writer(Objects objects) {
    return writer.apply(objects);
  }
  
  @Override
  public IdSupplier id() {
    return ID_SUPPLIER;
  }

  @Override
  public Delete<T> delete(Objects from) {
    return delete.apply(from);
  }
}

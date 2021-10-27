package io.resys.hdes.client;

import io.resys.hdes.client.api.HdesAstTypes;

/*-
 * #%L
 * hdes-client
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.spi.store.HdesDocumentStore;
import io.resys.hdes.client.spi.store.PersistenceConfig;

public class HdesClientImpl implements HdesClient {

  
  
  private final PersistenceConfig config;
  
  public HdesClientImpl(PersistenceConfig config) {
    this.config = config;
  }

  @Override
  public AstBuilder ast() {
    // TODO Auto-generated method stub
    throw new RuntimeException("Need to implemented the client");
  }

  @Override
  public ProgramBuilder program() {
    // TODO Auto-generated method stub
    throw new RuntimeException("Need to implemented the client");
  }

  @Override
  public ExecutorBuilder executor() {
    // TODO Auto-generated method stub
    throw new RuntimeException("Need to implemented the client");
  }

  @Override
  public HdesStore store() {
    return new HdesDocumentStore(config);
  }

  @Override
  public HdesAstTypes types() {
    // TODO Auto-generated method stub
    throw new RuntimeException("Need to implemented the client");
  }

  @Override
  public CSVBuilder csv() {
    // TODO Auto-generated method stub
    throw new RuntimeException("Need to implemented the client");
  }

  @Override
  public EnvirBuilder envir() {
    // TODO Auto-generated method stub
    return null;
  }

}

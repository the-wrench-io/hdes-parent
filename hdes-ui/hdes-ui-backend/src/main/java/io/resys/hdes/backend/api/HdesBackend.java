package io.resys.hdes.backend.api;

/*-
 * #%L
 * hdes-ui-backend
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

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

public interface HdesBackend {
  enum DefType { FW, DT, TG, ST, MT }
  enum ConfigType { LOCAL, REMOTE }
  
  List<Hierarchy> hierarchy();
  List<Search> search();
  Status status();
  
  DefQueryBuilder query();
  DefCreateBuilder builder();
  DefChangeBuilder change();
  
  Writer writer();
  Reader reader();
  
  interface Writer {
    byte[] build(Object value);
  }
  interface Reader {
    
  }
  
  interface DefChangeBuilder {
    DefCreateBuilder add(DefChangeEntry def);
    List<Def> build();
  }
  
  interface DefQueryBuilder {
    List<Def> find();
  }
  
  interface DefCreateBuilder {
    DefCreateBuilder add(DefCreateEntry def);
    List<Def> build();
  }
  
  @Value.Immutable
  interface DefChangeEntry {
    String getId();
    String getValue();
  }
  
  @Value.Immutable
  interface DefCreateEntry {
    String getName();
    DefType getType();
  }

  @Value.Immutable
  interface Def {
    String getId();
    DefAst getAst();
    String getName();
    DefType getType();
    String getValue();
  }
  
  @Value.Immutable
  interface DefAst {
    String getType();
    String getValue();
    Optional<DefAst> getNext();
  }
  
  @Value.Immutable
  interface Hierarchy {
    Def getDef();
    List<Def> getIn();
    List<Def> getOut();
  }
  
  @Value.Immutable
  interface Search {
    String getId();
    List<SearchEntry> getValues();
  }
  
  @Value.Immutable
  interface SearchEntry {
    String getType();
    String getValue();
  }
  
  @Value.Immutable
  interface Status {
    ConfigType getConfig();
    String getBranch();
  }
}

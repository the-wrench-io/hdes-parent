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

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface HdesUIBackend {
  enum DefType { FW, DT, TG, ST, MT }
  enum ConfigType { LOCAL, REMOTE }
  
  DefsQuery defs();
  List<Hierarchy> hierarchy();
  List<Search> search();
  Status status();
  DefBuilder builder();
  
  interface DefBuilderEntry {
    String getName();
    DefType getType();
  }

  interface DefBuilder {
    DefBuilder add(DefBuilderEntry def);
    List<Def> build();
  }
  
  interface Writer {
    Writer from(List<Def> defs);
    void build(ByteArrayOutputStream out);
  }
  
  interface DefsQuery {
    List<Def> find();
  }
  
  interface Def {
    String getId();
    String getName();
    DefType getType();
    String getValue();
  }
  
  interface Hierarchy {
    Def getDef();
    List<Def> getIn();
    List<Def> getOut();
  }
  
  interface Search {
    String getId();
    List<SearchEntry> getValues();
  }
  
  interface SearchEntry {
    String getType();
    String getValue();
  }
  
  interface Status {
    ConfigType getConfig();
    String getBranch();
  }
}

package io.resys.hdes.backend.api;

import java.io.Serializable;
import java.util.Collection;

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

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.ast.api.nodes.BodyNode.TypeDef;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;

public interface HdesBackend {
  enum DefType { FL, DT, ST }
  enum ConfigType { LOCAL, REMOTE, CLASSPATH }
  
  List<Hierarchy> hierarchy();
  List<Search> search();
  Status status();
  
  DefDebugBuilder debug();
  DefQueryBuilder query();
  DefCreateBuilder builder();
  DefChangeBuilder change();
  DefDeleteBuilder delete();
  
  Writer writer();
  Reader reader();
  
  interface Writer {
    byte[] build(Object value);
  }
  interface Reader {
    <T> T build(byte[] body, Class<T> type);
    <T> List<T> list(byte[] body, Class<T> type);
  }
  
  interface DefDebugBuilder {
    DefDebugBuilder qualifier(String tagOrBranch);
    DefDebugBuilder name(String name);
    DefDebugBuilder input(byte[] input);
    DefDebug build();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableDefDebug.class)
  @JsonDeserialize(as = ImmutableDefDebug.class)
  interface DefDebug {
    String getQualifier();
    String getName();
    @Nullable
    Serializable getOutput();
    List<DefError> getErrors();
    // Only set in error cases
    List<Resource> getResources();
  }
  
  interface DefChangeBuilder {
    DefChangeBuilder add(DefChangeEntry def);
    List<Def> build();
  }
  
  interface DefQueryBuilder {
    Collection<Def> find();
  }
  
  interface DefCreateBuilder {
    DefCreateBuilder add(DefCreateEntry def);
    DefCreateBuilder add(List<DefCreateEntry> def);
    List<Def> build();
  }
  
  interface DefDeleteBuilder {
    DefDeleteBuilder add(String defId);
    DefDeleteBuilder simulation(boolean simulation);
    DefDeleteBuilder entry(DefDeleteEntry entry);
    List<Def> build();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableDefDeleteEntry.class)
  @JsonDeserialize(as = ImmutableDefDeleteEntry.class)
  interface DefDeleteEntry {
    List<String> getId();
    Boolean getSimulation();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableDefChangeEntry.class)
  @JsonDeserialize(as = ImmutableDefChangeEntry.class)
  interface DefChangeEntry {
    String getId();
    String getValue();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableDefCreateEntry.class)
  @JsonDeserialize(as = ImmutableDefCreateEntry.class)
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
    List<DefError> getErrors();
  }
  
  @Value.Immutable
  interface DefAst {
    List<TypeDef> getInputs();
    
    //String getType();
    //String getValue();
    //Optional<DefAst> getNext();
  }
  
  @Value.Immutable
  interface DefError {
    String getId();
    String getName();
    String getMessage();
    DefErrorToken getToken();
  }
  
  @Value.Immutable
  interface DefErrorToken {
    int getLine();
    int getColumn();
    String getMsg();
    String getText();
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
    String getName();
    List<SearchEntry> getValues();
  }
  
  @Value.Immutable
  interface SearchEntry {
    String getType();
    String getValue();
  }
  
  @Value.Immutable
  interface Status {
    StorageConfig getStorage();
    List<DefError> getErrors();
    List<StatusMessage> getValues();
  }
  
  @Value.Immutable
  interface StatusMessage {
    String getId();
    String getValue();
    Optional<String> getLogCode();
    Optional<String> getLogStack();
  }
  
  interface StorageConfig {
    ConfigType getType();
  }
  
  @Value.Immutable
  interface LocalStorageConfig extends StorageConfig {
    String getLocation();
  }
  
  @Value.Immutable
  interface ClasspathStorageConfig extends StorageConfig {
  }
}

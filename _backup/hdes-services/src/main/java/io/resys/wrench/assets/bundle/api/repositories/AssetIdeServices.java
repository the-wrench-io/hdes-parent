package io.resys.wrench.assets.bundle.api.repositories;

/*-
 * #%L
 * wrench-assets-ide-services
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Migration;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public interface AssetIdeServices {
  
  AssetResourceQuery query();
  AssetSummary summary();
  Map<ServiceType, List<ServiceDataModel>> models();

  AssetResource remove(AssetResource asset);
  AssetResource persist(AssetResource resource);
  AssetResource copyAs(AssetCopyAs copyAs);
  String debug(AssetDebug debug);
  JsonNode commands(AssetCommand input);
  
  Migration migrate();
  
  interface AssetResourceQuery {
    AssetResourceQuery id(String id);
    AssetResourceQuery name(String name);
    AssetResourceQuery rev(String rev);
    AssetResourceQuery type(ServiceType type);
    Collection<AssetResource> build();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableAssetDebug.class)
  @JsonDeserialize(as = ImmutableAssetDebug.class)
  interface AssetDebug {
    String getId();
    @Nullable
    String getInput();
    @Nullable
    String getInputCsv();
    @Nullable
    String getContent();
    ServiceType getType();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableAssetCopyAs.class)
  @JsonDeserialize(as = ImmutableAssetCopyAs.class)
  interface AssetCopyAs {
    String getId();
    String getName();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableAssetError.class)
  @JsonDeserialize(as = ImmutableAssetError.class)
  interface AssetError {
    String getId();
    String getMessage();
  }
  
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableAssetResource.class)
  @JsonDeserialize(as = ImmutableAssetResource.class)
  interface AssetResource {
    @Nullable
    String getId();
    @Nullable
    String getName();
    ServiceType getType();
    String getContent();
    List<AssetError> getErrors();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateAssetResource.class)
  @JsonDeserialize(as = ImmutableCreateAssetResource.class)
  interface CreateAssetResource {
    String getName();
    ServiceType getType();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableUpdateAssetResource.class)
  @JsonDeserialize(as = ImmutableUpdateAssetResource.class)
  interface UpdateAssetResource {
    String getContent();
    ServiceType getType();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableAssetSummary.class)
  @JsonDeserialize(as = ImmutableAssetSummary.class)
  interface AssetSummary {
    JsonNode getOutput();
    String getCurrentHash();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableAssetCommand.class)
  @JsonDeserialize(as = ImmutableAssetCommand.class)
  interface AssetCommand {
    @Nullable
    JsonNode getInput();
    @Nullable
    Integer getRev();
    ServiceType getType();
  }
}

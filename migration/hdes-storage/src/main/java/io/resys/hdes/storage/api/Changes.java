package io.resys.hdes.storage.api;

/*-
 * #%L
 * hdes-storage
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

import java.io.Serializable;
import java.util.Collection;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.reactivex.annotations.Nullable;
import io.resys.hdes.datatype.api.DataTypeCommand;

@Value.Immutable
@JsonSerialize(as = ImmutableChanges.class)
@JsonDeserialize(as = ImmutableChanges.class)
public interface Changes extends Serializable {
  @Nullable
  String getId();
  @Nullable
  String getTenant();
  @Nullable
  String getLabel();
  @Nullable
  String getHash();
  
  Collection<DataTypeCommand> getValues();
}

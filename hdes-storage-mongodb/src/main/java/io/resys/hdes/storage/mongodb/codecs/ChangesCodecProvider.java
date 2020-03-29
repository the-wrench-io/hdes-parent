package io.resys.hdes.storage.mongodb.codecs;

/*-
 * #%L
 * hdes-storage-mongodb
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

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.storage.api.Changes;

public class ChangesCodecProvider implements CodecProvider {

  private final ChangeCodec changeCode = new ChangeCodec();
  private final ChangesCodec changesCodec = new ChangesCodec(changeCode);
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry)  {
    if(Changes.class.isAssignableFrom(clazz)) {
      return (Codec<T>) changesCodec;
    }
    if(DataTypeCommand.class.isAssignableFrom(clazz)) {
      return (Codec<T>) changeCode;
    }
    return null;
  }
}

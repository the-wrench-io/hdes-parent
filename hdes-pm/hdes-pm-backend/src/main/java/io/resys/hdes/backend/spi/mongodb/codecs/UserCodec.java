package io.resys.hdes.backend.spi.mongodb.codecs;

import java.time.LocalDateTime;
import java.util.Optional;

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

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import io.resys.hdes.backend.api.ImmutableUser;
import io.resys.hdes.backend.api.PmRepository.User;


public class UserCodec implements Codec<User> {
  
  public static final String ID = "_id";
  public static final String REV = "rev";
  public static final String EXTERNAL_ID = "externalId";
  private static final String CREATED = "created";
  public static final String VALUE = "value";
  
  @Override
  public void encode(BsonWriter writer, User command, EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString(ID, command.getId());
    writer.writeString(REV, command.getRev());
    writer.writeString(CREATED, command.getCreated().toString());
    writer.writeString(VALUE, command.getValue());
    
    if (command.getExternalId().isPresent()) {
      writer.writeString(EXTERNAL_ID, command.getExternalId().get());
    } else {
      writer.writeNull(EXTERNAL_ID);
    }
    
    writer.writeEndDocument();
  }

  @Override
  public User decode(BsonReader reader, DecoderContext decoderContext) {
    reader.readStartDocument();
    User result = ImmutableUser.builder()
      .id(reader.readString(ID))
      .rev(reader.readString(REV))
      .created(LocalDateTime.parse(reader.readString(CREATED)))
      .value(reader.readString(VALUE))
      .externalId(Optional.ofNullable(isNull(EXTERNAL_ID, reader) ? null : reader.readString()))
      .build();
    reader.readEndDocument();
    return result;
  }

  private boolean isNull(String id, BsonReader reader) {
    reader.readName(id);
    if (reader.getCurrentBsonType() == BsonType.NULL) {
      reader.readNull();
      return true;
    }
    return false;
  }

  @Override
  public Class<User> getEncoderClass() {
    return User.class;
  }
}
